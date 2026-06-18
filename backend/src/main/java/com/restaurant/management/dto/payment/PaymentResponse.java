package com.restaurant.management.dto.payment;

import com.restaurant.management.entity.payment.PaymentMethod;
import com.restaurant.management.entity.payment.PaymentStatus;
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
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private String transactionCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
