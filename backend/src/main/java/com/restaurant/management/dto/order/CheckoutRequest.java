package com.restaurant.management.dto.order;

import com.restaurant.management.entity.order.OrderType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull(message = "Order type is required")
    private OrderType orderType;

    private Long tableId;
    private String couponCode;
    private String note;
}
