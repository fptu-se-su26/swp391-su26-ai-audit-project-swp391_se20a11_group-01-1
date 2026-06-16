package com.restaurant.management.service;

import com.restaurant.management.dto.cart.AddCartItemRequest;
import com.restaurant.management.dto.cart.CartResponse;
import com.restaurant.management.dto.cart.UpdateCartItemRequest;

public interface CartService {
    CartResponse getCart();
    CartResponse addItem(AddCartItemRequest request);
    CartResponse updateItemQuantity(Long itemId, UpdateCartItemRequest request);
    CartResponse removeItem(Long itemId);
    void clearCart();
}
