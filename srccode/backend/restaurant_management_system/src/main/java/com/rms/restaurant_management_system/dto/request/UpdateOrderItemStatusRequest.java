package com.rms.restaurant_management_system.dto.request;

import com.rms.restaurant_management_system.enums.OrderItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderItemStatusRequest {

    @NotNull(message = "Order item status is required")
    private OrderItemStatus status;
}
