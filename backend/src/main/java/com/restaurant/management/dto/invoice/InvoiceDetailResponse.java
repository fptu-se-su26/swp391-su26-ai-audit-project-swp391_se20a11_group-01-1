package com.restaurant.management.dto.invoice;

import com.restaurant.management.dto.order.OrderDetailResponse;
import com.restaurant.management.dto.payment.PaymentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailResponse {
    private Long id;
    private String invoiceNumber;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime issuedAt;
    
    private PaymentResponse payment;
    private OrderDetailResponse order;
}
