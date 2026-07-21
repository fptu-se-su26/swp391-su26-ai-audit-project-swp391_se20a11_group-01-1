package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.PaymentRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.PaymentResponse;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentMethod;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import com.rms.restaurant_management_system.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.webhooks.WebhookData;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PayOS payOS;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Transactional
    public PaymentResponse payOrder(Long orderId, PaymentRequest request) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (paymentRepository.findLockedByOrderOrderId(orderId).isPresent()) {
            throw new RuntimeException("This order has already been paid");
        }

        if (order.getStatus() != OrderStatus.READY) {
            throw new RuntimeException("Only READY orders can be paid");
        }

        Payment payment = Payment.builder()
                .order(order)
                .method(request.getMethod())
                .status(PaymentStatus.PAID)
                .amount(order.getTotalAmount())
                .transactionCode(request.getTransactionCode())
                .note(request.getNote())
                .paidAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        completeOrder(orderId);

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse createPayOSPayment(Long orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.READY) {
            throw new RuntimeException("Only READY orders can be paid");
        }

        Payment existingPayment = paymentRepository.findLockedByOrderOrderId(orderId)
                .orElse(null);

        if (existingPayment != null) {
            if (existingPayment.getStatus() == PaymentStatus.PAID) {
                throw new RuntimeException("This order has already been paid");
            }

            if (existingPayment.getStatus() == PaymentStatus.PENDING) {
                return mapToResponse(existingPayment);
            }
        }

        Long payosOrderCode = generatePayOSOrderCode(order.getOrderId());

        Long amount = order.getTotalAmount()
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        String description = "ORD " + order.getOrderId();

        String returnUrl = frontendUrl + "/staff/orders?payment=success&orderId=" + orderId;
        String cancelUrl = frontendUrl + "/staff/orders?payment=cancel&orderId=" + orderId;

        try {
            CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                    .orderCode(payosOrderCode)
                    .amount(amount)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .build();

            var paymentLink = payOS.paymentRequests().create(request);

            Payment payment = Payment.builder()
                    .order(order)
                    .method(PaymentMethod.QR)
                    .status(PaymentStatus.PENDING)
                    .amount(order.getTotalAmount())
                    .payosOrderCode(payosOrderCode)
                    .paymentLinkId(paymentLink.getPaymentLinkId())
                    .checkoutUrl(paymentLink.getCheckoutUrl())
                    .qrCode(paymentLink.getQrCode())
                    .note("PayOS QR payment pending")
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            return mapToResponse(savedPayment);

        } catch (Exception exception) {
            throw new RuntimeException("Cannot create PayOS payment link: " + exception.getMessage());
        }
    }

    @Override
    @Transactional
    public void handlePayOSWebhook(Map<String, Object> webhookBody) {
        try {
            WebhookData data = payOS.webhooks().verify(webhookBody);
            Long payosOrderCode = data.getOrderCode();

            if (payosOrderCode == null) {
                throw new RuntimeException("Missing orderCode in webhook");
            }

            Payment payment = paymentRepository.findLockedByPayosOrderCode(payosOrderCode)
                    .orElseThrow(() -> new RuntimeException(
                            "Payment not found by PayOS orderCode: " + payosOrderCode
                    ));

            if (payment.getStatus() == PaymentStatus.PAID) {
                return;
            }

            long expectedAmount = payment.getAmount().setScale(0, RoundingMode.HALF_UP).longValueExact();
            if (data.getAmount() == null || data.getAmount() != expectedAmount) {
                throw new RuntimeException("Webhook amount does not match the order total");
            }

            if (data.getPaymentLinkId() != null && payment.getPaymentLinkId() != null
                    && !payment.getPaymentLinkId().equals(data.getPaymentLinkId())) {
                throw new RuntimeException("Webhook payment link does not match");
            }

            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionCode(data.getReference());
            payment.setPaidAt(LocalDateTime.now());
            payment.setNote("PayOS payment success");

            Payment savedPayment = paymentRepository.save(payment);

            completeOrder(savedPayment.getOrder().getOrderId());

        } catch (Exception exception) {
            throw new RuntimeException("Invalid PayOS webhook: " + exception.getMessage());
        }
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void completeOrder(Long orderId) {
        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
        updateRequest.setStatus(OrderStatus.COMPLETED);

        orderService.updateOrderStatus(orderId, updateRequest);
    }

    private Long generatePayOSOrderCode(Long orderId) {
        long randomPart = SECURE_RANDOM.nextLong(100_000L, 1_000_000L);
        return Math.addExact(Math.multiplyExact(System.currentTimeMillis(), 1_000_000L), randomPart)
                & Long.MAX_VALUE;
    }

    private PaymentResponse mapToResponse(Payment payment) {
        Order order = payment.getOrder();

        return new PaymentResponse(
                payment.getPaymentId(),
                order.getOrderId(),
                order.getOrderCode(),
                order.getTableId(),
                order.getTableName(),
                order.getCustomerName(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getPayosOrderCode(),
                payment.getPaymentLinkId(),
                payment.getCheckoutUrl(),
                payment.getQrCode(),
                payment.getTransactionCode(),
                payment.getNote(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}
