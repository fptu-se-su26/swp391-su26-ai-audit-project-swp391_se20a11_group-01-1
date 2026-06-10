package com.restaurant.dto.kitchen;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class KitchenOrderResponse {
    private Long orderId;
    private String orderStatus;
    private String orderType;
    private String customerName;
    private String staffName;
    private BigDecimal totalAmount;
    private String note;
    private LocalDateTime createdAt;
    private List<KitchenOrderItemResponse> items;
}
