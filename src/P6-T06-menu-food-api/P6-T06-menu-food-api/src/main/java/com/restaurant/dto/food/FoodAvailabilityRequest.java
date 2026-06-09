package com.restaurant.dto.food;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodAvailabilityRequest {
    @NotNull(message = "Available status is required")
    private Boolean available;
}
