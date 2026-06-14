package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.CartItemRequest;
import com.rms.restaurant_management_system.dto.request.UpdateCartItemRequest;
import com.rms.restaurant_management_system.dto.response.CartItemResponse;
import com.rms.restaurant_management_system.dto.response.CartResponse;
import com.rms.restaurant_management_system.entity.Cart;
import com.rms.restaurant_management_system.entity.CartItem;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.FoodStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.CartItemRepository;
import com.rms.restaurant_management_system.repository.CartRepository;
import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.interfaces.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    @Override
    @Transactional
    public CartResponse getMyCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(String userEmail, CartItemRequest request) {
        Cart cart = getOrCreateCart(userEmail);
        
        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));
                
        if (food.getStatus() != FoodStatus.AVAILABLE) {
            throw new BadRequestException("This food is currently not available for sale");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndFoodId(cart.getId(), food.getId());
        
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .food(food)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        return mapToResponse(cartRepository.findById(cart.getId()).get());
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(String userEmail, Long itemId, UpdateCartItemRequest request) {
        CartItem cartItem = getCartItemAndVerifyOwner(userEmail, itemId);
        
        if (request.getQuantity() == 0) {
            cartItem.getCart().getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }
        
        return mapToResponse(cartRepository.findById(cartItem.getCart().getId()).get());
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(String userEmail, Long itemId) {
        CartItem cartItem = getCartItemAndVerifyOwner(userEmail, itemId);
        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        return mapToResponse(cartRepository.findById(cart.getId()).get());
    }

    @Override
    @Transactional
    public void clearCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail).orElseGet(() -> {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Cart newCart = Cart.builder()
                    .user(user)
                    .build();
            return cartRepository.save(newCart);
        });
    }

    private CartItem getCartItemAndVerifyOwner(String userEmail, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
                
        if (!cartItem.getCart().getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to modify this cart item");
        }
        return cartItem;
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemResponses)
                .subtotal(subtotal)
                .createdAt(cart.getCreatedAt())
                .build();
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        BigDecimal unitPrice = item.getFood().getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        
        return CartItemResponse.builder()
                .id(item.getId())
                .foodId(item.getFood().getId())
                .foodName(item.getFood().getName())
                .foodImageUrl(item.getFood().getImageUrl())
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .lineTotal(lineTotal)
                .build();
    }
}
