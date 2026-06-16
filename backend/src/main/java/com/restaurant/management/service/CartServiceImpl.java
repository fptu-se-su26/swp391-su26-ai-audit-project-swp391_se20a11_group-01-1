package com.restaurant.management.service;

import com.restaurant.management.dto.cart.AddCartItemRequest;
import com.restaurant.management.dto.cart.CartItemResponse;
import com.restaurant.management.dto.cart.CartResponse;
import com.restaurant.management.dto.cart.UpdateCartItemRequest;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.cart.Cart;
import com.restaurant.management.entity.cart.CartItem;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.CartItemRepository;
import com.restaurant.management.repository.CartRepository;
import com.restaurant.management.repository.FoodItemRepository;
import com.restaurant.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {
        User currentUser = getCurrentUser();
        Optional<Cart> cartOpt = cartRepository.findByUserId(currentUser.getId());
        
        if (cartOpt.isEmpty()) {
            return CartResponse.builder()
                    .userId(currentUser.getId())
                    .items(List.of())
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }
        
        return mapToResponse(cartOpt.get());
    }

    @Override
    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(currentUser)
                            .build();
                    return cartRepository.save(newCart);
                });

        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        if (foodItem.getDeletedAt() != null || !foodItem.getIsAvailable()) {
            throw new BadRequestException("Food item is not available");
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getFoodItem().getId().equals(foodItem.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setUnitPrice(foodItem.getPrice()); // Update snapshot price
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .foodItem(foodItem)
                    .quantity(request.getQuantity())
                    .unitPrice(foodItem.getPrice())
                    .build();
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long itemId, UpdateCartItemRequest request) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        cartItem.setQuantity(request.getQuantity());
        // Do not update the snapshotted price on quantity update unless business rules require it,
        // but typically snapshot happens at add time. Wait, the prompt says "tại thời điểm add/update".
        cartItem.setUnitPrice(cartItem.getFoodItem().getPrice());
        
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long itemId) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        cart.removeItem(cartItem);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public void clearCart() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User is not authenticated");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> {
                    BigDecimal subTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return CartItemResponse.builder()
                            .id(item.getId())
                            .foodItemId(item.getFoodItem().getId())
                            .foodName(item.getFoodItem().getName())
                            .foodImageUrl(item.getFoodItem().getImageUrl())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subTotal(subTotal)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }
}
