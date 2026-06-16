package com.restaurant.management.service;

import com.restaurant.management.dto.cart.AddCartItemRequest;
import com.restaurant.management.dto.cart.CartResponse;
import com.restaurant.management.dto.cart.UpdateCartItemRequest;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.cart.Cart;
import com.restaurant.management.entity.cart.CartItem;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.repository.CartItemRepository;
import com.restaurant.management.repository.CartRepository;
import com.restaurant.management.repository.FoodItemRepository;
import com.restaurant.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private FoodItem foodItem;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder().username("testuser").build();
        user.setId(1L);

        foodItem = FoodItem.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("10.00"))
                .isAvailable(true)
                .build();

        cart = Cart.builder().id(1L).user(user).build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // We only mock these for tests that actually reach getCurrentUser()
        // but to avoid strict stubbing errors, we'll set lenient or just mock inline when needed.
    }

    private void mockAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void testAddItem_Success() {
        mockAuthentication();
        AddCartItemRequest request = new AddCartItemRequest();
        request.setFoodItemId(1L);
        request.setQuantity(2);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.addItem(request);

        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("20.00"), response.getTotalAmount());
    }

    @Test
    void testAddItem_SameFoodIncrementsQuantity() {
        mockAuthentication();
        
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .foodItem(foodItem)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();
        cart.addItem(existingItem);

        AddCartItemRequest request = new AddCartItemRequest();
        request.setFoodItemId(1L);
        request.setQuantity(2);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.addItem(request);

        assertEquals(1, response.getItems().size());
        assertEquals(3, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("30.00"), response.getTotalAmount());
    }

    @Test
    void testAddItem_UnavailableFoodFails() {
        mockAuthentication();
        foodItem.setIsAvailable(false);
        
        AddCartItemRequest request = new AddCartItemRequest();
        request.setFoodItemId(1L);
        request.setQuantity(1);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));

        assertThrows(BadRequestException.class, () -> cartService.addItem(request));
    }

    @Test
    void testUpdateItemQuantity_Success() {
        mockAuthentication();
        CartItem existingItem = CartItem.builder()
                .id(100L)
                .cart(cart)
                .foodItem(foodItem)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();
        cart.addItem(existingItem);

        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(5);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.updateItemQuantity(100L, request);

        assertEquals(5, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("50.00"), response.getTotalAmount());
    }

    @Test
    void testRemoveItem_Success() {
        mockAuthentication();
        CartItem existingItem = CartItem.builder()
                .id(100L)
                .cart(cart)
                .foodItem(foodItem)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();
        cart.addItem(existingItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.removeItem(100L);

        assertEquals(0, response.getItems().size());
        assertEquals(new BigDecimal("0"), response.getTotalAmount());
    }

    @Test
    void testClearCart_Success() {
        mockAuthentication();
        CartItem existingItem = CartItem.builder()
                .id(100L)
                .cart(cart)
                .foodItem(foodItem)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();
        cart.addItem(existingItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart();

        verify(cartRepository).save(cart);
        assertEquals(0, cart.getItems().size());
    }
}
