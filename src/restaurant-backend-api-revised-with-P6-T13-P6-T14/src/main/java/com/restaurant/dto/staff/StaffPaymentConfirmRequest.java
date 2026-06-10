package com.restaurant.dto.staff;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffPaymentConfirmRequest {
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod; // CASH, QR, CARD, BANK_TRANSFER, ONLINE

    private String transactionCode;
}
