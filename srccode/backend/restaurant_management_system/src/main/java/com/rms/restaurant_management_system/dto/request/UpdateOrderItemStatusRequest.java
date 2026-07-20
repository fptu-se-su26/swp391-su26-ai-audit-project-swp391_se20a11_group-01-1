package com.rms.restaurant_management_system.dto.request;

import com.rms.restaurant_management_system.enums.OrderItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderItemStatusRequest {
    @NotNull(message = "Status cannot be null")
    private OrderItemStatus status;
}
