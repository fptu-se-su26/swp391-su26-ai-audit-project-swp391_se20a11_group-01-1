package com.restaurant.dto.staff;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffPaymentResponse {
    private Long paymentId;
    private Long orderId;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal amount;
    private String transactionCode;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
