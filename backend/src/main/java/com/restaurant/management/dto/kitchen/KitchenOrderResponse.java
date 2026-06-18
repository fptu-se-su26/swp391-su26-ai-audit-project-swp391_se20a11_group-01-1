package com.restaurant.management.dto.kitchen;

import com.restaurant.management.dto.order.OrderItemResponse;

import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenOrderResponse {
    private Long id;
    private Long tableId;
    private OrderStatus orderStatus;
    private OrderType orderType;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
}
