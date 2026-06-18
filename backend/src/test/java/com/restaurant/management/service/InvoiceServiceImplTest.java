package com.restaurant.management.service;

import com.restaurant.management.dto.invoice.InvoiceDetailResponse;
import com.restaurant.management.dto.invoice.InvoiceResponse;
import com.restaurant.management.entity.auth.Role;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.invoice.Invoice;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.entity.payment.Payment;
import com.restaurant.management.entity.payment.PaymentStatus;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.InvoiceRepository;
import com.restaurant.management.repository.PaymentRepository;
import com.restaurant.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private User user;
    private Payment payment;
    private RestaurantOrder order;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("customer1");
        
        GrantedAuthority auth = () -> "ROLE_CUSTOMER";
        Collection<GrantedAuthority> authorities = Collections.singletonList(auth);
        lenient().when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        
        SecurityContextHolder.setContext(securityContext);

        user = new User();
        user.setId(1L);
        user.setUsername("customer1");

        order = new RestaurantOrder();
        order.setId(10L);
        order.setCustomer(user);
        order.setItems(new ArrayList<>());

        payment = new Payment();
        payment.setId(100L);
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setAmount(new BigDecimal("200000"));
    }

    @Test
    void testGenerateInvoice_Success() {
        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));
        when(invoiceRepository.findByPaymentId(100L)).thenReturn(Optional.empty());

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice inv = invocation.getArgument(0);
            inv.setId(1L);
            return inv;
        });

        InvoiceResponse response = invoiceService.generateInvoice(100L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getPaymentId());
        assertEquals(new BigDecimal("200000"), response.getTotalAmount());
        assertTrue(response.getInvoiceNumber().startsWith("INV-"));
        
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testGenerateInvoice_PaymentNotFound() {
        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(paymentRepository.findById(100L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> invoiceService.generateInvoice(100L));
        assertEquals("Payment not found", ex.getMessage());
    }

    @Test
    void testGenerateInvoice_PaymentNotPaid() {
        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> invoiceService.generateInvoice(100L));
        assertEquals("Cannot generate invoice for a payment that is not PAID", ex.getMessage());
    }

    @Test
    void testGenerateInvoice_Idempotent() {
        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(2L);
        existingInvoice.setPayment(payment);
        existingInvoice.setInvoiceNumber("INV-OLD");
        existingInvoice.setTotalAmount(new BigDecimal("200000"));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));
        when(invoiceRepository.findByPaymentId(100L)).thenReturn(Optional.of(existingInvoice));

        InvoiceResponse response = invoiceService.generateInvoice(100L);

        assertEquals(2L, response.getId());
        assertEquals("INV-OLD", response.getInvoiceNumber());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void testGenerateInvoice_OtherUserInvoice_Fails() {
        User otherUser = new User();
        otherUser.setId(2L);
        order.setCustomer(otherUser);

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> invoiceService.generateInvoice(100L));
        assertEquals("Payment not found or does not belong to you", ex.getMessage());
    }

    @Test
    void testGetInvoiceById_Success() {
        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(2L);
        existingInvoice.setPayment(payment);
        existingInvoice.setInvoiceNumber("INV-OLD");
        existingInvoice.setTotalAmount(new BigDecimal("200000"));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(invoiceRepository.findById(2L)).thenReturn(Optional.of(existingInvoice));

        InvoiceDetailResponse response = invoiceService.getInvoiceById(2L);

        assertEquals(2L, response.getId());
        assertEquals("INV-OLD", response.getInvoiceNumber());
        assertEquals(100L, response.getPayment().getId());
    }
}
