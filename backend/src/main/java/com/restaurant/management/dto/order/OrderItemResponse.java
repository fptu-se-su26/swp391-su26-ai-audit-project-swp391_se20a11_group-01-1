package com.restaurant.management.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long foodItemId;
    private String foodName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String note;
}
