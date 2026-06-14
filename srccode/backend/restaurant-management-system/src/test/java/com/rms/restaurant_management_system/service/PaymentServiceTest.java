package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.PaymentRequest;
import com.rms.restaurant_management_system.dto.response.PaymentResponse;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentMethod;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        order = Order.builder()
                .id(1L)
                .user(user)
                .finalTotal(new BigDecimal("100.00"))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    void processPayment_Success() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethod.CREDIT_CARD, false);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(1L, PaymentStatus.PAID)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request, "test@example.com");

        assertNotNull(response);
        assertEquals(PaymentStatus.PAID, response.getStatus());
        assertEquals(OrderStatus.PREPARING, order.getStatus());
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(order);
    }

    @Test
    void processPayment_FailureSimulation() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethod.CREDIT_CARD, true);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(1L, PaymentStatus.PAID)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request, "test@example.com");

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());
        assertEquals(OrderStatus.PENDING, order.getStatus()); // Order status remains unchanged
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository, never()).save(order);
    }

    @Test
    void processPayment_AlreadyPaid_ShouldThrowException() {
        PaymentRequest request = new PaymentRequest(1L, PaymentMethod.CREDIT_CARD, false);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(1L, PaymentStatus.PAID)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> paymentService.processPayment(request, "test@example.com"));
    }

    @Test
    void processPayment_OrderNotInPending_ShouldThrowException() {
        order.setStatus(OrderStatus.PREPARING);
        PaymentRequest request = new PaymentRequest(1L, PaymentMethod.CREDIT_CARD, false);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrderIdAndStatus(1L, PaymentStatus.PAID)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> paymentService.processPayment(request, "test@example.com"));
    }

    @Test
    void processPayment_CancelledOrder_ShouldThrowException() {
        order.setStatus(OrderStatus.CANCELLED);
        PaymentRequest request = new PaymentRequest(1L, PaymentMethod.CREDIT_CARD, false);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> paymentService.processPayment(request, "test@example.com"));
    }
}
