package com.restaurant.management.dto.menu;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFoodStatusRequest {
    @NotNull(message = "isAvailable is required")
    private Boolean isAvailable;
}
