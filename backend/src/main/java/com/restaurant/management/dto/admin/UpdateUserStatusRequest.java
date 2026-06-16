package com.restaurant.management.dto.admin;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateUserStatusRequest {
    @NotNull(message = "Status is required")
    private UserStatus status;
}
