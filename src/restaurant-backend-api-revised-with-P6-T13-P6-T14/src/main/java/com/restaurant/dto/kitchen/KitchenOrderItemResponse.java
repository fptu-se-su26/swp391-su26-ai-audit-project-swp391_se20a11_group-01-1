package com.restaurant.dto.kitchen;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class KitchenOrderItemResponse {
    private Long orderItemId;
    private Long orderId;
    private Long foodId;
    private String foodName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String itemStatus;
    private String kitchenNote;
    private Integer estimatedCookingTime;
}
