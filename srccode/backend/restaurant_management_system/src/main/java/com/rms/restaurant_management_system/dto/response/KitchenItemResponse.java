package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenItemResponse {
    private Long orderItemId;
    private Long orderId;
    private String orderCode;
    private String tableName;
    private Long foodId;
    private String foodName;
    private Integer quantity;
    private String note;
    private OrderItemStatus status;
    private LocalDateTime orderCreatedAt;
}
