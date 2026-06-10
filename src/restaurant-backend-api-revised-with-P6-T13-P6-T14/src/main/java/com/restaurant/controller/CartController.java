package com.restaurant.controller;
import com.restaurant.common.ApiResponse; import com.restaurant.dto.cart.*; import com.restaurant.service.CartService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/cart") @RequiredArgsConstructor
public class CartController { private final CartService cartService;
 @GetMapping public ApiResponse<CartResponse> get(){ return ApiResponse.ok("My cart", cartService.getMyCart()); }
 @PostMapping("/items") public ApiResponse<CartResponse> add(@Valid @RequestBody AddCartItemRequest r){ return ApiResponse.ok("Cart item added", cartService.add(r)); }
 @PutMapping("/items/{itemId}") public ApiResponse<CartResponse> update(@PathVariable Long itemId,@Valid @RequestBody UpdateCartItemRequest r){ return ApiResponse.ok("Cart item updated", cartService.update(itemId,r)); }
 @DeleteMapping("/items/{itemId}") public ApiResponse<CartResponse> remove(@PathVariable Long itemId){ return ApiResponse.ok("Cart item removed", cartService.remove(itemId)); }
 @DeleteMapping public ApiResponse<Void> clear(){ cartService.clear(); return ApiResponse.ok("Cart cleared", null); }
}
