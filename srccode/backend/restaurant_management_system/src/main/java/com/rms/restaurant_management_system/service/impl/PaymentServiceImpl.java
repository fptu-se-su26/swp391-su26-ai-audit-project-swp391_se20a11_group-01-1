package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.PaymentRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.PaymentResponse;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentMethod;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import com.rms.restaurant_management_system.service.interfaces.PaymentService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.security.SecureRandom;
import com.rms.restaurant_management_system.error.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PayOS payOS;
    private final EntityManager entityManager;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
@Transactional
public PaymentResponse payOrder(Long orderId, PaymentRequest request) {
    if (paymentRepository.findLockedByOrderOrderId(orderId).isPresent()) {
        throw new ResourceConflictException(
                ErrorCode.PAYMENT_ALREADY_PROCESSED,
                "Đơn hàng đã được thanh toán"
        );
    }

    Order order = reloadReadyOrderForPayment(orderId);
    BigDecimal latestAmount = order.getTotalAmount();

        Payment payment = Payment.builder()
                .order(order)
                .method(request.getMethod())
                .status(PaymentStatus.PAID)
                .amount(latestAmount)
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
    Payment existingPayment = paymentRepository
            .findLockedByOrderOrderId(orderId)
            .orElse(null);

    Order order = reloadReadyOrderForPayment(orderId);
    BigDecimal latestAmount = order.getTotalAmount();
        if (existingPayment != null) {
            if (existingPayment.getStatus() == PaymentStatus.PAID) {
                throw new ResourceConflictException(ErrorCode.PAYMENT_ALREADY_PROCESSED, "Đơn hàng đã được thanh toán");
            }

            if (existingPayment.getStatus() == PaymentStatus.PENDING) {
                return mapToResponse(existingPayment);
            }
        }

        Order order = reloadReadyOrderForPayment(orderId);
        BigDecimal latestAmount = order.getTotalAmount();

        Long payosOrderCode = generatePayOSOrderCode(order.getOrderId());

        Long amount = latestAmount
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
                    .amount(latestAmount)
                    .payosOrderCode(payosOrderCode)
                    .paymentLinkId(paymentLink.getPaymentLinkId())
                    .checkoutUrl(paymentLink.getCheckoutUrl())
                    .qrCode(paymentLink.getQrCode())
                    .note("PayOS QR payment pending")
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            return mapToResponse(savedPayment);

        } catch (Exception exception) {
            throw new ExternalServiceException("Không thể tạo liên kết thanh toán PayOS", exception);
        }
    }

    @Override
    @Transactional
    public void handlePayOSWebhook(Map<String, Object> webhookBody) {
        try {
            if (!Boolean.TRUE.equals(webhookBody.get("success"))) {
                return;
            }
            WebhookData data = payOS.webhooks().verify(webhookBody);
            Long payosOrderCode = data.getOrderCode();

            if (payosOrderCode == null) {
                throw new BusinessRuleException("Webhook PayOS thiếu orderCode");
            }

            Payment candidate = paymentRepository.findByPayosOrderCode(payosOrderCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch PayOS"));

            if (candidate.getStatus() == PaymentStatus.PAID) {
                return;
            }

            Long orderId = candidate.getOrder().getOrderId();
            orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
            Payment payment = paymentRepository.findLockedByPayosOrderCode(payosOrderCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thanh toán"));

            if (payment.getStatus() == PaymentStatus.PAID) {
                return;
            }

            long expectedAmount = payment.getAmount().setScale(0, RoundingMode.HALF_UP).longValueExact();
            if (data.getAmount() == null || data.getAmount() != expectedAmount) {
                throw new BusinessRuleException("Webhook amount does not match order total");
            }

            if (data.getPaymentLinkId() != null && payment.getPaymentLinkId() != null
                    && !payment.getPaymentLinkId().equals(data.getPaymentLinkId())) {
                throw new BusinessRuleException("Liên kết thanh toán trong webhook không khớp");
            }

            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionCode(data.getReference());
            payment.setPaidAt(LocalDateTime.now());
            payment.setNote("PayOS payment success");

            Payment savedPayment = paymentRepository.save(payment);

            completeOrder(orderId);

        } catch (ApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessRuleException("invalid signature in PayOS webhook");
        }
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thanh toán"));

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

    private Order reloadReadyOrderForPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        entityManager.refresh(order);

        if (order.getStatus() != OrderStatus.READY) {
            throw new RuntimeException("Only READY orders can be paid");
        }

        boolean hasActiveItems = order.getItems().stream()
                .anyMatch(item -> item.getStatus() != OrderItemStatus.CANCELLED);

        boolean hasUnreadyActiveItem = order.getItems().stream()
                .anyMatch(item -> item.getStatus() != OrderItemStatus.CANCELLED
                        && item.getStatus() != OrderItemStatus.READY);

        if (!hasActiveItems || hasUnreadyActiveItem) {
            throw new RuntimeException(
                    "All non-cancelled order items must be READY before payment"
            );
        }

        return order;
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
