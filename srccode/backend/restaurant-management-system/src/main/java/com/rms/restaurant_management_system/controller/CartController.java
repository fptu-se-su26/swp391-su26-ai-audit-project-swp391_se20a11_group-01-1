package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.CartItemRequest;
import com.rms.restaurant_management_system.dto.request.UpdateCartItemRequest;
import com.rms.restaurant_management_system.dto.response.CartResponse;
import com.rms.restaurant_management_system.service.interfaces.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> getMyCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getMyCart(authentication.getName()));
    }

    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody CartItemRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(cartService.addItemToCart(authentication.getName(), request));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> updateItemQuantity(@PathVariable Long itemId,
                                                           @Valid @RequestBody UpdateCartItemRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.ok(cartService.updateItemQuantity(authentication.getName(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long itemId,
                                                           Authentication authentication) {
        return ResponseEntity.ok(cartService.removeItemFromCart(authentication.getName(), itemId));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
