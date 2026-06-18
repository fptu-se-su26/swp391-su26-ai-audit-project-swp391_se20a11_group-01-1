package com.restaurant.management.service;

import com.restaurant.management.dto.payment.PaymentConfirmRequest;
import com.restaurant.management.dto.payment.PaymentResponse;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.entity.payment.Payment;
import com.restaurant.management.entity.payment.PaymentStatus;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.OrderRepository;
import com.restaurant.management.repository.PaymentRepository;
import com.restaurant.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentResponse confirmPayment(Long orderId, PaymentConfirmRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RestaurantOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Check if user owns the order
        if (!order.getCustomer().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Order not found or does not belong to you");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot pay for a " + order.getOrderStatus().name().toLowerCase() + " order");
        }

        // Check amount match
        if (order.getTotalAmount().compareTo(request.getAmount()) != 0) {
            throw new BadRequestException("Payment amount does not match order total amount");
        }

        // Idempotency: if already paid, return existing payment
        Optional<Payment> existingPaid = paymentRepository.findByOrderIdAndPaymentStatus(orderId, PaymentStatus.PAID);
        if (existingPaid.isPresent()) {
            return mapToResponse(existingPaid.get());
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PAID) // Set to PAID since it's confirmed
                .amount(request.getAmount())
                .transactionCode(request.getPaymentReference())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update Order Status to CONFIRMED
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Note: Coupon usage updating would happen here or after completed order, but per rules:
        // "Không update coupon_usages trong task này trừ khi đã có rule rõ; nếu chưa chắc, ghi rõ sẽ làm ở Order/Payment integration sau."
        // We will skip updating coupon_usages in this task.

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RestaurantOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Check ownership
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getCustomer().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Order not found or does not belong to you");
        }

        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .transactionCode(payment.getTransactionCode())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
