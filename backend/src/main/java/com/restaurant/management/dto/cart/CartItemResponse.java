package com.restaurant.management.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long foodItemId;
    private String foodName;
    private String foodImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}
