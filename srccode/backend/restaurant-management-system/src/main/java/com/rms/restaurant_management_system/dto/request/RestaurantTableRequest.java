package com.rms.restaurant_management_system.dto.request;

import com.rms.restaurant_management_system.enums.TableStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTableRequest {

    @NotNull(message = "Table number is required")
    @Min(value = 1, message = "Table number must be greater than 0")
    private Integer number;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be greater than 0")
    private Integer capacity;

    private TableStatus status;
}
