package com.restaurant.management.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelOrderRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
