package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.PaymentRequest;
import com.rms.restaurant_management_system.dto.response.PaymentResponse;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, String userEmail) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to pay for this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot pay for a cancelled order");
        }

        boolean alreadyPaid = paymentRepository.existsByOrderIdAndStatus(order.getId(), PaymentStatus.PAID);
        if (alreadyPaid || order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is already paid or in progress");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getFinalTotal())
                .method(request.getMethod())
                .transactionId(UUID.randomUUID().toString())
                .build();

        if (request.isSimulateFailure()) {
            payment.setStatus(PaymentStatus.FAILED);
            Payment savedPayment = paymentRepository.save(payment);
            return mapToResponse(savedPayment);
        }

        payment.setStatus(PaymentStatus.PAID);
        Payment savedPayment = paymentRepository.save(payment);

        // Update Order status
        order.setStatus(OrderStatus.PREPARING);
        orderRepository.save(order);

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId, String userEmail) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if (!payment.getOrder().getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to view this payment");
        }

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
