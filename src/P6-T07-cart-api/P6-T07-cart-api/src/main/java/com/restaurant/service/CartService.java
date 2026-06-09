package com.restaurant.service;

import com.restaurant.dto.cart.*;
import com.restaurant.entity.Cart;
import com.restaurant.entity.CartItem;
import com.restaurant.entity.FoodItem;
import com.restaurant.entity.User;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.CartItemRepository;
import com.restaurant.repository.CartRepository;
import com.restaurant.repository.FoodItemRepository;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return toCartResponse(cart);
    }

    public CartResponse addItem(AddCartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        // Nếu entity FoodItem có field available/status khác, sửa điều kiện này cho đúng schema của nhóm.
        if (foodItem.getAvailable() != null && !foodItem.getAvailable()) {
            throw new BadRequestException("Food item is not available");
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItem.getId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .foodItem(foodItem)
                    .quantity(request.getQuantity())
                    .price(foodItem.getPrice())
                    .build();
            cartItemRepository.save(cartItem);
        }

        return toCartResponse(getOrCreateCart(user));
    }

    public CartResponse updateItem(Long cartItemId, UpdateCartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        validateCartOwner(cartItem, cart);
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return toCartResponse(cart);
    }

    public CartResponse removeItem(Long cartItemId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        validateCartOwner(cartItem, cart);
        cartItemRepository.delete(cartItem);

        return toCartResponse(cart);
    }

    public CartResponse clearCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCartId(cart.getId());
        return toCartResponse(cart);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .user(user)
                        .cartItems(new ArrayList<>())
                        .build()));
    }

    private void validateCartOwner(CartItem cartItem, Cart cart) {
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("This cart item does not belong to current user");
        }
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toCartItemResponse)
                .toList();

        int totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        double totalAmount = itemResponses.stream()
                .mapToDouble(CartItemResponse::getLineTotal)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .customerId(cart.getUser().getId())
                .items(itemResponses)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        FoodItem food = cartItem.getFoodItem();
        double price = cartItem.getPrice() != null ? cartItem.getPrice() : food.getPrice();
        int quantity = cartItem.getQuantity();

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .foodItemId(food.getId())
                .foodName(food.getName())
                .imageUrl(food.getImageUrl())
                .price(price)
                .quantity(quantity)
                .lineTotal(price * quantity)
                .build();
    }
}
