package com.restaurant.management.service;

import com.restaurant.management.dto.coupon.CouponValidationRequest;
import com.restaurant.management.dto.coupon.CouponValidationResponse;
import com.restaurant.management.dto.order.CancelOrderRequest;
import com.restaurant.management.dto.order.CheckoutRequest;
import com.restaurant.management.dto.order.OrderDetailResponse;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.cart.Cart;
import com.restaurant.management.entity.cart.CartItem;
import com.restaurant.management.entity.coupon.Coupon;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.OrderType;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.CartRepository;
import com.restaurant.management.repository.CouponRepository;
import com.restaurant.management.repository.FoodItemRepository;
import com.restaurant.management.repository.OrderRepository;
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
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponService couponService;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private FoodItem foodItem;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("customer1");
        SecurityContextHolder.setContext(securityContext);

        user = new User();
        user.setId(1L);
        user.setUsername("customer1");

        foodItem = new FoodItem();
        foodItem.setId(10L);
        foodItem.setName("Burger");
        foodItem.setPrice(new BigDecimal("50000"));
        foodItem.setIsAvailable(true);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        
        CartItem cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setCart(cart);
        cartItem.setFoodItem(foodItem);
        cartItem.setQuantity(2);
        
        cart.setItems(new ArrayList<>());
        cart.getItems().add(cartItem);
    }

    @Test
    void testCheckout_Success_WithoutCoupon() {
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderType(OrderType.TAKEAWAY);

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(foodItem));
        when(orderRepository.save(any(RestaurantOrder.class))).thenAnswer(invocation -> {
            RestaurantOrder order = invocation.getArgument(0);
            order.setId(1000L);
            return order;
        });

        OrderDetailResponse response = orderService.checkout(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100000"), response.getSubTotal());
        assertEquals(new BigDecimal("0"), response.getDiscountAmount());
        assertEquals(new BigDecimal("100000"), response.getTotalAmount());
        assertEquals(OrderType.TAKEAWAY, response.getOrderType());

        verify(cartService).clearCart(); // Verify clear cart is called
    }

    @Test
    void testCheckout_Success_WithCoupon() {
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderType(OrderType.DELIVERY);
        request.setCouponCode("SUMMER");

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(foodItem));
        
        CouponValidationResponse valResp = new CouponValidationResponse();
        valResp.setValid(true);
        valResp.setDiscountAmount(new BigDecimal("20000"));
        valResp.setFinalAmount(new BigDecimal("80000"));
        when(couponService.validateCoupon(any(CouponValidationRequest.class))).thenReturn(valResp);
        
        Coupon coupon = new Coupon();
        coupon.setCode("SUMMER");
        when(couponRepository.findByCode("SUMMER")).thenReturn(Optional.of(coupon));

        when(orderRepository.save(any(RestaurantOrder.class))).thenAnswer(invocation -> {
            RestaurantOrder order = invocation.getArgument(0);
            order.setId(1000L);
            return order;
        });

        OrderDetailResponse response = orderService.checkout(request);

        assertEquals(new BigDecimal("100000"), response.getSubTotal());
        assertEquals(new BigDecimal("20000"), response.getDiscountAmount());
        assertEquals(new BigDecimal("80000"), response.getTotalAmount());
        assertEquals("SUMMER", response.getCouponCode());
    }

    @Test
    void testCheckout_EmptyCart_Fails() {
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderType(OrderType.DINE_IN);

        cart.getItems().clear(); // empty cart

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> orderService.checkout(request));
        assertEquals("Cart is empty", ex.getMessage());
    }

    @Test
    void testCheckout_UnavailableFood_Fails() {
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderType(OrderType.DINE_IN);

        foodItem.setIsAvailable(false); // food unavailable

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(foodItem));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> orderService.checkout(request));
        assertTrue(ex.getMessage().contains("is not available"));
    }

    @Test
    void testGetMyOrder_AnotherUser_Fails() {
        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        // Order ID 1 belongs to someone else, so it won't be found for this customer
        when(orderRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> orderService.getMyOrder(1L));
        assertTrue(ex.getMessage().contains("Order not found"));
    }

    @Test
    void testCancelOrder_Success() {
        RestaurantOrder order = new RestaurantOrder();
        order.setId(1L);
        order.setCustomer(user);
        order.setOrderStatus(OrderStatus.PENDING);

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(RestaurantOrder.class))).thenReturn(order);

        CancelOrderRequest req = new CancelOrderRequest();
        req.setReason("Changed mind");

        orderService.cancelOrder(1L, req);

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertTrue(order.getNote().contains("Cancel reason: Changed mind"));
    }

    @Test
    void testCancelOrder_Completed_Fails() {
        RestaurantOrder order = new RestaurantOrder();
        order.setId(1L);
        order.setCustomer(user);
        order.setOrderStatus(OrderStatus.COMPLETED); // Equivalent to paid/completed rule

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(order));

        CancelOrderRequest req = new CancelOrderRequest();
        req.setReason("Too late");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L, req));
        assertEquals("Cannot cancel a completed order", ex.getMessage());
    }
}
