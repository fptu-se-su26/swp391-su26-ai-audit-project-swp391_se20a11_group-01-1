package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class KitchenItemResponse {

    private Long orderItemId;
    private Long orderId;
    private String orderCode;
    private Long tableId;
    private String tableName;
    private Long foodId;
    private String foodName;
    private Integer quantity;
    private String imageUrl;
    private String emoji;
    private String note;
    private OrderItemStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime statusUpdatedAt;
}
