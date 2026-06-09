package com.restaurant.dto.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long foodItemId;
    private String foodName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double lineTotal;
}
