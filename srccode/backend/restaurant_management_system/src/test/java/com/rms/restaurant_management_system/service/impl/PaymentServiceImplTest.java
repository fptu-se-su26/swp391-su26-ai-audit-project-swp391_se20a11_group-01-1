package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.PaymentRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.PaymentResponse;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentMethod;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.service.blocking.v2.paymentRequests.PaymentRequestsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private PayOS payOS;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PaymentRequestsService paymentRequestsService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @ParameterizedTest
    @EnumSource(value = OrderItemStatus.class, names = {"CONFIRMED", "PREPARING"})
    void cashPaymentIsRejectedWhenAnyActiveItemIsNotReady(
            OrderItemStatus unreadyStatus
    ) {
        Order order = order(
                new BigDecimal("10000"),
                item(11L, unreadyStatus, "10000")
        );

        when(paymentRepository.findLockedByOrderOrderId(1L)).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.payOrder(1L, cashRequest())
        );

        assertTrue(exception.getMessage().contains("must be READY"));
        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(orderService);
    }

    @Test
    void payOsPaymentIsRejectedWhenAnActiveItemIsPreparing() {
        Order order = order(
                new BigDecimal("10000"),
                item(11L, OrderItemStatus.PREPARING, "10000")
        );

        when(paymentRepository.findLockedByOrderOrderId(1L)).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.createPayOSPayment(1L)
        );

        assertTrue(exception.getMessage().contains("must be READY"));
        verifyNoInteractions(payOS);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void cashPaymentUsesLatestTotalWhenAllActiveItemsAreReady() {
        BigDecimal latestTotal = new BigDecimal("35000");
        Order order = order(
                latestTotal,
                item(11L, OrderItemStatus.READY, "35000"),
                item(12L, OrderItemStatus.CANCELLED, "90000")
        );

        when(paymentRepository.findLockedByOrderOrderId(1L)).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.payOrder(1L, cashRequest());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(PaymentStatus.PAID, savedPayment.getStatus());
        assertEquals(latestTotal, savedPayment.getAmount());
        assertEquals(latestTotal, response.getAmount());

        ArgumentCaptor<UpdateOrderStatusRequest> statusCaptor =
                ArgumentCaptor.forClass(UpdateOrderStatusRequest.class);
        verify(orderService).updateOrderStatus(org.mockito.ArgumentMatchers.eq(1L), statusCaptor.capture());
        assertEquals(OrderStatus.COMPLETED, statusCaptor.getValue().getStatus());

        InOrder reloadOrder = inOrder(paymentRepository, orderRepository);
        reloadOrder.verify(paymentRepository).findLockedByOrderOrderId(1L);
        reloadOrder.verify(orderRepository).findById(1L);
        verify(entityManager).refresh(order);
    }

    @Test
    void payOsPaymentUsesLatestTotalWithoutChangingIntegrationContract() {
        BigDecimal latestTotal = new BigDecimal("35500.60");
        Order order = order(
                latestTotal,
                item(11L, OrderItemStatus.READY, "35500.60")
        );
        CreatePaymentLinkResponse linkResponse = mock(CreatePaymentLinkResponse.class);
        when(linkResponse.getPaymentLinkId()).thenReturn("link-1");
        when(linkResponse.getCheckoutUrl()).thenReturn("https://pay.example/checkout");
        when(linkResponse.getQrCode()).thenReturn("qr-data");
        ReflectionTestUtils.setField(
                paymentService,
                "frontendUrl",
                "http://localhost:3000"
        );

        when(paymentRepository.findLockedByOrderOrderId(1L)).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(payOS.paymentRequests()).thenReturn(paymentRequestsService);
        when(paymentRequestsService.create(any(CreatePaymentLinkRequest.class)))
                .thenReturn(linkResponse);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayOSPayment(1L);

        ArgumentCaptor<CreatePaymentLinkRequest> requestCaptor =
                ArgumentCaptor.forClass(CreatePaymentLinkRequest.class);
        verify(paymentRequestsService).create(requestCaptor.capture());
        CreatePaymentLinkRequest payOsRequest = requestCaptor.getValue();

        assertEquals(35501L, payOsRequest.getAmount());
        assertEquals("ORD 1", payOsRequest.getDescription());
        assertEquals(
                "http://localhost:3000/staff/orders?payment=success&orderId=1",
                payOsRequest.getReturnUrl()
        );
        assertEquals(
                "http://localhost:3000/staff/orders?payment=cancel&orderId=1",
                payOsRequest.getCancelUrl()
        );

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.PENDING, paymentCaptor.getValue().getStatus());
        assertEquals(latestTotal, paymentCaptor.getValue().getAmount());
        assertEquals(latestTotal, response.getAmount());
        assertEquals("link-1", response.getPaymentLinkId());
    }

    private PaymentRequest cashRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setMethod(PaymentMethod.CASH);
        return request;
    }

    private Order order(BigDecimal totalAmount, OrderItem... items) {
        Order order = Order.builder()
                .orderId(1L)
                .orderCode("ORD-1")
                .status(OrderStatus.READY)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        return order;
    }

    private OrderItem item(Long id, OrderItemStatus status, String subtotal) {
        return OrderItem.builder()
                .orderItemId(id)
                .foodId(id)
                .foodName("Food " + id)
                .unitPrice(new BigDecimal(subtotal))
                .quantity(1)
                .subtotal(new BigDecimal(subtotal))
                .status(status)
                .build();
    }
}
