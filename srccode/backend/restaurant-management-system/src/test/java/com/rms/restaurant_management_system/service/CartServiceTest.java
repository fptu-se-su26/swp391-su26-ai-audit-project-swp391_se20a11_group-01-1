package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.CartItemRequest;
import com.rms.restaurant_management_system.dto.request.UpdateCartItemRequest;
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
import com.rms.restaurant_management_system.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;
    private Food food;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        cart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();
        food = Food.builder().id(1L).name("Pizza").price(new BigDecimal("10.50")).status(FoodStatus.AVAILABLE).build();
        cartItem = CartItem.builder().id(1L).cart(cart).food(food).quantity(2).build();
    }

    @Test
    void getMyCart_WhenCartExists_ShouldReturnCart() {
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        
        CartResponse response = cartService.getMyCart("test@example.com");
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getMyCart_WhenCartDoesNotExist_ShouldCreateAndReturnCart() {
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        CartResponse response = cartService.getMyCart("test@example.com");
        
        assertNotNull(response);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCart_WhenNewItem_ShouldSaveNewItem() {
        CartItemRequest request = new CartItemRequest(1L, 2);
        
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(cartItemRepository.findByCartIdAndFoodId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.addItemToCart("test@example.com", request);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_WhenItemExists_ShouldUpdateQuantity() {
        CartItemRequest request = new CartItemRequest(1L, 3);
        cart.getItems().add(cartItem);
        
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(cartItemRepository.findByCartIdAndFoodId(1L, 1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.addItemToCart("test@example.com", request);

        assertEquals(5, cartItem.getQuantity()); // 2 + 3
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void addItemToCart_WhenFoodNotAvailable_ShouldThrowException() {
        food.setStatus(FoodStatus.UNAVAILABLE);
        CartItemRequest request = new CartItemRequest(1L, 1);
        
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        assertThrows(BadRequestException.class, () -> cartService.addItemToCart("test@example.com", request));
    }

    @Test
    void updateItemQuantity_WhenQuantityGreaterThanZero_ShouldUpdate() {
        UpdateCartItemRequest request = new UpdateCartItemRequest(5);
        cart.getItems().add(cartItem);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.updateItemQuantity("test@example.com", 1L, request);

        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void updateItemQuantity_WhenQuantityIsZero_ShouldDelete() {
        UpdateCartItemRequest request = new UpdateCartItemRequest(0);
        cart.getItems().add(cartItem);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.updateItemQuantity("test@example.com", 1L, request);

        assertTrue(cart.getItems().isEmpty());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void updateItemQuantity_WhenNotOwner_ShouldThrowException() {
        UpdateCartItemRequest request = new UpdateCartItemRequest(5);
        User otherUser = User.builder().id(2L).email("other@example.com").build();
        cart.setUser(otherUser);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        assertThrows(AccessDeniedException.class, () -> cartService.updateItemQuantity("test@example.com", 1L, request));
    }

    @Test
    void removeItemFromCart_Success() {
        cart.getItems().add(cartItem);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart("test@example.com", 1L);

        assertTrue(cart.getItems().isEmpty());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void clearCart_Success() {
        cart.getItems().add(cartItem);
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));

        cartService.clearCart("test@example.com");

        assertTrue(cart.getItems().isEmpty());
        verify(cartItemRepository).deleteAll(anyList());
        verify(cartRepository).save(cart);
    }

    @Test
    void calculateSubtotal_ShouldBeCorrect() {
        cart.getItems().add(cartItem); // 2 * 10.50 = 21.00
        
        Food food2 = Food.builder().id(2L).name("Burger").price(new BigDecimal("5.00")).status(FoodStatus.AVAILABLE).build();
        CartItem cartItem2 = CartItem.builder().id(2L).cart(cart).food(food2).quantity(3).build(); // 3 * 5.00 = 15.00
        cart.getItems().add(cartItem2);

        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        
        CartResponse response = cartService.getMyCart("test@example.com");

        assertEquals(0, new BigDecimal("36.00").compareTo(response.getSubtotal()));
    }
}
