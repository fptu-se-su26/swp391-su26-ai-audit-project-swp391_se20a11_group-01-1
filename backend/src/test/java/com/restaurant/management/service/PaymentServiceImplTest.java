package com.restaurant.management.service;

import com.restaurant.management.dto.payment.PaymentConfirmRequest;
import com.restaurant.management.dto.payment.PaymentResponse;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.entity.payment.Payment;
import com.restaurant.management.entity.payment.PaymentMethod;
import com.restaurant.management.entity.payment.PaymentStatus;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.OrderRepository;
import com.restaurant.management.repository.PaymentRepository;
import com.restaurant.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private RestaurantOrder order;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("customer1");
        
        // Mock getAuthorities to return an empty collection to avoid NullPointerException
        org.springframework.security.core.GrantedAuthority auth = () -> "ROLE_CUSTOMER";
        java.util.Collection<org.springframework.security.core.GrantedAuthority> authorities = java.util.Collections.singletonList(auth);
        org.mockito.Mockito.lenient().when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        SecurityContextHolder.setContext(securityContext);

        user = new User();
        user.setId(1L);
        user.setUsername("customer1");

        order = new RestaurantOrder();
        order.setId(100L);
        order.setCustomer(user);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("150000"));
    }

    @Test
    void testConfirmPayment_Success() {
        PaymentConfirmRequest req = new PaymentConfirmRequest();
        req.setPaymentMethod(PaymentMethod.QR);
        req.setAmount(new BigDecimal("150000"));
        req.setPaymentReference("TXN123");

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndPaymentStatus(100L, PaymentStatus.PAID)).thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentResponse response = paymentService.confirmPayment(100L, req);

        assertNotNull(response);
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());
        assertEquals("TXN123", response.getTransactionCode());
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus()); // Order status updated
        verify(orderRepository).save(order);
    }

    @Test
    void testConfirmPayment_AmountMismatch_Fails() {
        PaymentConfirmRequest req = new PaymentConfirmRequest();
        req.setPaymentMethod(PaymentMethod.CASH);
        req.setAmount(new BigDecimal("100000")); // Mismatch with 150000

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> paymentService.confirmPayment(100L, req));
        assertEquals("Payment amount does not match order total amount", ex.getMessage());
    }

    @Test
    void testConfirmPayment_CancelledOrder_Fails() {
        order.setOrderStatus(OrderStatus.CANCELLED);

        PaymentConfirmRequest req = new PaymentConfirmRequest();
        req.setPaymentMethod(PaymentMethod.QR);
        req.setAmount(new BigDecimal("150000"));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> paymentService.confirmPayment(100L, req));
        assertEquals("Cannot pay for a cancelled order", ex.getMessage());
    }

    @Test
    void testConfirmPayment_Idempotent_ReturnsExisting() {
        PaymentConfirmRequest req = new PaymentConfirmRequest();
        req.setPaymentMethod(PaymentMethod.ONLINE_SIMULATION);
        req.setAmount(new BigDecimal("150000"));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        Payment existingPayment = new Payment();
        existingPayment.setId(2L);
        existingPayment.setOrder(order);
        existingPayment.setPaymentStatus(PaymentStatus.PAID);
        existingPayment.setAmount(new BigDecimal("150000"));

        when(paymentRepository.findByOrderIdAndPaymentStatus(100L, PaymentStatus.PAID)).thenReturn(Optional.of(existingPayment));

        PaymentResponse response = paymentService.confirmPayment(100L, req);

        assertEquals(2L, response.getId());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());

        // Should not save a new payment
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testConfirmPayment_OtherUserOrder_Fails() {
        User otherUser = new User();
        otherUser.setId(2L);
        order.setCustomer(otherUser);

        PaymentConfirmRequest req = new PaymentConfirmRequest();
        req.setPaymentMethod(PaymentMethod.CASH);
        req.setAmount(new BigDecimal("150000"));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> paymentService.confirmPayment(100L, req));
        assertEquals("Order not found or does not belong to you", ex.getMessage());
    }

    @Test
    void testGetPayments_Success() {
        Payment payment = new Payment();
        payment.setId(5L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("150000"));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(100L)).thenReturn(Collections.singletonList(payment));

        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(100L);

        assertEquals(1, responses.size());
        assertEquals(5L, responses.get(0).getId());
    }
}
