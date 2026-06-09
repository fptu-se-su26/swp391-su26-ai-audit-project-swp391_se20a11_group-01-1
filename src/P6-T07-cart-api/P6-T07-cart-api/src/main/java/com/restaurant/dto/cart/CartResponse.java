package com.restaurant.dto.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CartResponse {
    private Long cartId;
    private Long customerId;
    private List<CartItemResponse> items;
    private Integer totalItems;
    private Double totalAmount;
}
