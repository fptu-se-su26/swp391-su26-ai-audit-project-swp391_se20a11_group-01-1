package com.restaurant.management.dto.staff;

import com.restaurant.management.entity.order.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StaffCreateOrderRequest {
    @NotNull(message = "Order type is required")
    private OrderType orderType;

    private Long tableId;

    private String note;

    @NotEmpty(message = "Items cannot be empty")
    @Valid
    private List<StaffOrderItemRequest> items;
}
