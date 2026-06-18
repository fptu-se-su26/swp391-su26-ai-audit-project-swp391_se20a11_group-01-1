package com.restaurant.management.dto.staff;

import com.restaurant.management.entity.order.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StaffUpdateOrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
