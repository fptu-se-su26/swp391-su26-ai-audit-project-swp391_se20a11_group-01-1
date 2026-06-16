package com.restaurant.management.dto.menu;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCategoryStatusRequest {
    @NotNull(message = "Status is required (true for active, false for deleted/inactive)")
    private Boolean isActive;
}
