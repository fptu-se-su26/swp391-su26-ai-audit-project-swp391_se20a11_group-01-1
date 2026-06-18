package com.restaurant.management.service;

import com.restaurant.management.dto.invoice.InvoiceDetailResponse;
import com.restaurant.management.dto.invoice.InvoiceResponse;
import com.restaurant.management.dto.order.OrderDetailResponse;
import com.restaurant.management.dto.order.OrderItemResponse;
import com.restaurant.management.dto.payment.PaymentResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public InvoiceResponse generateInvoice(Long paymentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Check ownership
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !payment.getOrder().getCustomer().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Payment not found or does not belong to you");
        }

        if (payment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BadRequestException("Cannot generate invoice for a payment that is not PAID");
        }

        // Idempotency: if invoice already exists, return it
        Optional<Invoice> existingInvoice = invoiceRepository.findByPaymentId(paymentId);
        if (existingInvoice.isPresent()) {
            return mapToResponse(existingInvoice.get());
        }

        String invoiceNumber = "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-P" + paymentId;

        Invoice invoice = Invoice.builder()
                .payment(payment)
                .invoiceNumber(invoiceNumber)
                .taxAmount(java.math.BigDecimal.ZERO)
                .totalAmount(payment.getAmount())
                .issuedAt(LocalDateTime.now())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return mapToResponse(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        checkOwnership(invoice.getPayment());
        return mapToDetailResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceByPaymentId(Long paymentId) {
        Invoice invoice = invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for payment"));
        checkOwnership(invoice.getPayment());
        return mapToDetailResponse(invoice);
    }

    private void checkOwnership(Payment payment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !payment.getOrder().getCustomer().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Invoice not found or does not belong to you");
        }
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .paymentId(invoice.getPayment().getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .issuedAt(invoice.getIssuedAt())
                .build();
    }

    private InvoiceDetailResponse mapToDetailResponse(Invoice invoice) {
        Payment payment = invoice.getPayment();
        RestaurantOrder order = payment.getOrder();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(payment.getId())
                .orderId(order.getId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .transactionCode(payment.getTransactionCode())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();

        OrderDetailResponse orderResponse = OrderDetailResponse.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderType(order.getOrderType())
                .subTotal(order.getSubTotal())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .foodItemId(item.getFoodItem().getId())
                        .foodName(item.getFoodItem().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .note(item.getNote())
                        .build()).collect(Collectors.toList()))
                .build();

        return InvoiceDetailResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .issuedAt(invoice.getIssuedAt())
                .payment(paymentResponse)
                .order(orderResponse)
                .build();
    }
}
