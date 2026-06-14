package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.CartItemRequest;
import com.rms.restaurant_management_system.dto.request.UpdateCartItemRequest;
import com.rms.restaurant_management_system.dto.response.CartResponse;

public interface CartService {
    CartResponse getMyCart(String userEmail);
    CartResponse addItemToCart(String userEmail, CartItemRequest request);
    CartResponse updateItemQuantity(String userEmail, Long itemId, UpdateCartItemRequest request);
    CartResponse removeItemFromCart(String userEmail, Long itemId);
    void clearCart(String userEmail);
}
