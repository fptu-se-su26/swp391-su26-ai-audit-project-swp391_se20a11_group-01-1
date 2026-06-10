package com.restaurant.dto.staff;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffOrderStatusRequest {
    @NotBlank(message = "orderStatus is required")
    private String orderStatus; // PENDING, PENDING_PAYMENT, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED
}
