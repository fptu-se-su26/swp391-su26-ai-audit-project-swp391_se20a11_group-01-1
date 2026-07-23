package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.enums.*;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.service.impl.PaymentServiceImpl;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.payos.PayOS;
import vn.payos.model.webhooks.WebhookData;
import vn.payos.service.blocking.webhooks.WebhooksService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookVerificationTest {
    @Mock PaymentRepository paymentRepository;
    @Mock OrderRepository orderRepository;
    @Mock OrderService orderService;
    @Mock PayOS payOS;
    @Mock EntityManager entityManager;
    @Mock WebhooksService webhooksService;
    PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(
                paymentRepository,
                orderRepository,
                orderService,
                payOS,
                entityManager
        );
    }

    @Test
    void unsuccessfulWebhookNeverReachesSignatureOrDatabase() {
        paymentService.handlePayOSWebhook(Map.of("success", false));
        verifyNoInteractions(payOS, paymentRepository, orderRepository, orderService);
    }

    @Test
    void invalidSignatureCannotChangePayment() {
        Map<String, Object> body = Map.of("success", true, "signature", "forged");
        when(payOS.webhooks()).thenReturn(webhooksService);
        when(webhooksService.verify(body)).thenThrow(new RuntimeException("invalid signature"));

        assertThatThrownBy(() -> paymentService.handlePayOSWebhook(body))
                .hasMessageContaining("invalid signature");
        verifyNoInteractions(paymentRepository, orderRepository, orderService);
    }

    @Test
    void amountMismatchCannotCompleteOrder() {
        Map<String, Object> body = Map.of("success", true);
        Payment payment = pendingPayment();
        WebhookData data = webhookData(999L);
        when(payOS.webhooks()).thenReturn(webhooksService);
        when(webhooksService.verify(body)).thenReturn(data);
        when(paymentRepository.findByPayosOrderCode(123L)).thenReturn(Optional.of(payment));
        when(orderRepository.findByOrderId(10L)).thenReturn(Optional.of(payment.getOrder()));
        when(paymentRepository.findLockedByPayosOrderCode(123L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.handlePayOSWebhook(body))
                .hasMessageContaining("amount does not match");
        verify(paymentRepository, never()).save(any());
        verifyNoInteractions(orderService);
    }

    @Test
    void validWebhookPaysAndCompletesExactlyOnce() {
        Map<String, Object> body = Map.of("success", true);
        Payment payment = pendingPayment();
        WebhookData data = webhookData(150_000L);
        when(payOS.webhooks()).thenReturn(webhooksService);
        when(webhooksService.verify(body)).thenReturn(data);
        when(paymentRepository.findByPayosOrderCode(123L)).thenReturn(Optional.of(payment));
        when(orderRepository.findByOrderId(10L)).thenReturn(Optional.of(payment.getOrder()));
        when(paymentRepository.findLockedByPayosOrderCode(123L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.handlePayOSWebhook(body);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(payment.getTransactionCode()).isEqualTo("TX-1");
        verify(paymentRepository).save(payment);
        verify(orderService).updateOrderStatus(eq(10L), argThat(request -> request.getStatus() == OrderStatus.COMPLETED));
    }

    @Test
    void repeatedPaidWebhookIsIdempotent() {
        Map<String, Object> body = Map.of("success", true);
        Payment payment = pendingPayment();
        payment.setStatus(PaymentStatus.PAID);
        when(payOS.webhooks()).thenReturn(webhooksService);
        when(webhooksService.verify(body)).thenReturn(webhookData(150_000L));
        when(paymentRepository.findByPayosOrderCode(123L)).thenReturn(Optional.of(payment));

        paymentService.handlePayOSWebhook(body);

        verify(paymentRepository, never()).findLockedByPayosOrderCode(anyLong());
        verify(paymentRepository, never()).save(any());
        verifyNoInteractions(orderRepository, orderService);
    }

    private Payment pendingPayment() {
        Order order = Order.builder().orderId(10L).orderCode("ORD-10").status(OrderStatus.READY)
                .totalAmount(new BigDecimal("150000.00")).build();
        return Payment.builder().paymentId(20L).order(order).method(PaymentMethod.QR)
                .status(PaymentStatus.PENDING).amount(order.getTotalAmount())
                .payosOrderCode(123L).paymentLinkId("LINK-1").build();
    }

    private WebhookData webhookData(long amount) {
        WebhookData data = new WebhookData();
        data.setOrderCode(123L);
        data.setAmount(amount);
        data.setPaymentLinkId("LINK-1");
        data.setReference("TX-1");
        return data;
    }
}
