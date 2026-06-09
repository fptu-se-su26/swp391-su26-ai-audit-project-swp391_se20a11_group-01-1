package com.restaurant.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderItemResponse {

    private Long orderItemId;
    private Long foodItemId;
    private String foodName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String itemStatus;
    private String note;
}
