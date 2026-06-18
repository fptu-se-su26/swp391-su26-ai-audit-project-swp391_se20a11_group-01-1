package com.restaurant.management.dto.kitchen;

import com.restaurant.management.entity.order.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KitchenUpdateOrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
