package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.CreateOrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.CouponValidationResponse;
import com.rms.restaurant_management_system.dto.response.OrderResponse;
import com.rms.restaurant_management_system.entity.*;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.repository.CartRepository;
import com.rms.restaurant_management_system.repository.CouponRepository;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import com.rms.restaurant_management_system.service.impl.OrderServiceImpl;
import com.rms.restaurant_management_system.service.interfaces.CartService;
import com.rms.restaurant_management_system.service.interfaces.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponService couponService;
    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private CartItem cartItem;
    private Food food;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        food = Food.builder().id(1L).name("Pizza").price(new BigDecimal("20.00")).build();
        cart = Cart.builder().id(1L).user(user).build();
        cartItem = CartItem.builder().id(1L).cart(cart).food(food).quantity(2).build();
        cart.setItems(List.of(cartItem));

        order = Order.builder()
                .id(1L)
                .user(user)
                .subtotal(new BigDecimal("40.00"))
                .discountAmount(BigDecimal.ZERO)
                .finalTotal(new BigDecimal("40.00"))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    void createOrderFromCart_Success_NoCoupon_NoReservation() {
        CreateOrderRequest request = new CreateOrderRequest();
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrderResponse response = orderService.createOrderFromCart("test@example.com", request);

        assertNotNull(response);
        assertEquals(0, new BigDecimal("40.00").compareTo(response.getSubtotal()));
        assertEquals(0, new BigDecimal("40.00").compareTo(response.getFinalTotal()));
        verify(cartService).clearCart("test@example.com");
    }

    @Test
    void createOrderFromCart_EmptyCart_ShouldThrowException() {
        cart.setItems(List.of());
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));

        assertThrows(BadRequestException.class, () -> orderService.createOrderFromCart("test@example.com", new CreateOrderRequest()));
    }

    @Test
    void createOrderFromCart_WithValidCoupon_ShouldApplyDiscount() {
        CreateOrderRequest request = new CreateOrderRequest(null, "DISCOUNT10");
        Coupon coupon = Coupon.builder().id(1L).code("DISCOUNT10").usedCount(0).build();
        
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        
        CouponValidationResponse validationResponse = new CouponValidationResponse(true, "OK", new BigDecimal("10.00"), new BigDecimal("30.00"));
        when(couponService.validateAndCalculateDiscount("DISCOUNT10", new BigDecimal("40.00"))).thenReturn(validationResponse);
        when(couponRepository.findByCode("DISCOUNT10")).thenReturn(Optional.of(coupon));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.createOrderFromCart("test@example.com", request);

        assertEquals(0, new BigDecimal("10.00").compareTo(response.getDiscountAmount()));
        assertEquals(0, new BigDecimal("30.00").compareTo(response.getFinalTotal()));
        assertEquals(1, coupon.getUsedCount());
        verify(couponRepository).save(coupon);
    }

    @Test
    void createOrderFromCart_WithInvalidCoupon_ShouldThrowException() {
        CreateOrderRequest request = new CreateOrderRequest(null, "INVALID");
        
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        
        CouponValidationResponse validationResponse = new CouponValidationResponse(false, "Invalid coupon", BigDecimal.ZERO, new BigDecimal("40.00"));
        when(couponService.validateAndCalculateDiscount("INVALID", new BigDecimal("40.00"))).thenReturn(validationResponse);

        assertThrows(BadRequestException.class, () -> orderService.createOrderFromCart("test@example.com", request));
    }

    @Test
    void cancelOrder_WhenPending_ShouldCancel() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.cancelOrder(1L, "test@example.com", false);

        assertEquals(OrderStatus.CANCELLED, response.getStatus());
    }

    @Test
    void cancelOrder_WhenNotPending_ShouldThrowException() {
        order.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.cancelOrder(1L, "test@example.com", false));
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.PREPARING);

        OrderResponse response = orderService.updateOrderStatus(1L, request);

        assertEquals(OrderStatus.PREPARING, response.getStatus());
    }
}
