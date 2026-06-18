package com.restaurant.management.service;

import com.restaurant.management.dto.staff.StaffCreateOrderRequest;
import com.restaurant.management.dto.staff.StaffOrderItemRequest;
import com.restaurant.management.dto.staff.StaffOrderResponse;
import com.restaurant.management.dto.staff.StaffUpdateOrderStatusRequest;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.OrderType;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.entity.reservation.RestaurantTable;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.repository.FoodItemRepository;
import com.restaurant.management.repository.OrderRepository;
import com.restaurant.management.repository.RestaurantTableRepository;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffOrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantTableRepository tableRepository;

    @InjectMocks
    private StaffOrderServiceImpl staffOrderService;

    private User staff;
    private FoodItem foodItem;
    private RestaurantTable table;
    private RestaurantOrder order;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("staff1");
        SecurityContextHolder.setContext(securityContext);

        staff = new User();
        staff.setId(1L);
        staff.setUsername("staff1");

        foodItem = new FoodItem();
        foodItem.setId(10L);
        foodItem.setName("Burger");
        foodItem.setPrice(new BigDecimal("50000"));
        foodItem.setIsAvailable(true);

        table = new RestaurantTable();
        table.setId(100L);

        order = new RestaurantOrder();
        order.setId(1000L);
        order.setCustomer(staff);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setItems(new ArrayList<>());
    }

    @Test
    void testCreateDineInOrder_Success() {
        StaffCreateOrderRequest request = new StaffCreateOrderRequest();
        request.setOrderType(OrderType.DINE_IN);
        request.setTableId(100L);

        StaffOrderItemRequest itemReq = new StaffOrderItemRequest();
        itemReq.setFoodItemId(10L);
        itemReq.setQuantity(2);
        request.setItems(Collections.singletonList(itemReq));

        when(userRepository.findByUsername("staff1")).thenReturn(Optional.of(staff));
        when(tableRepository.findById(100L)).thenReturn(Optional.of(table));
        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(foodItem));
        when(orderRepository.save(any(RestaurantOrder.class))).thenAnswer(inv -> {
            RestaurantOrder saved = inv.getArgument(0);
            saved.setId(1000L);
            return saved;
        });

        StaffOrderResponse response = staffOrderService.createOrder(request);

        assertEquals(1000L, response.getId());
        assertEquals(OrderType.DINE_IN, response.getOrderType());
        assertEquals(100L, response.getTableId());
        assertEquals(OrderStatus.CONFIRMED, response.getOrderStatus());
        assertEquals(new BigDecimal("100000"), response.getSubTotal()); // 50000 * 2
    }

    @Test
    void testCreateTakeawayOrder_Success() {
        StaffCreateOrderRequest request = new StaffCreateOrderRequest();
        request.setOrderType(OrderType.TAKEAWAY);

        StaffOrderItemRequest itemReq = new StaffOrderItemRequest();
        itemReq.setFoodItemId(10L);
        itemReq.setQuantity(1);
        request.setItems(Collections.singletonList(itemReq));

        when(userRepository.findByUsername("staff1")).thenReturn(Optional.of(staff));
        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(foodItem));
        when(orderRepository.save(any(RestaurantOrder.class))).thenAnswer(inv -> {
            RestaurantOrder saved = inv.getArgument(0);
            saved.setId(1001L);
            return saved;
        });

        StaffOrderResponse response = staffOrderService.createOrder(request);

        assertEquals(1001L, response.getId());
        assertEquals(OrderType.TAKEAWAY, response.getOrderType());
    }

    @Test
    void testCreateOrder_UnavailableFood_Fails() {
        foodItem.setIsAvailable(false);

        StaffCreateOrderRequest request = new StaffCreateOrderRequest();
        request.setOrderType(OrderType.TAKEAWAY);

        StaffOrderItemRequest itemReq = new StaffOrderItemRequest();
        itemReq.setFoodItemId(10L);
        itemReq.setQuantity(1);
        request.setItems(Collections.singletonList(itemReq));

        when(userRepository.findByUsername("staff1")).thenReturn(Optional.of(staff));
        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(foodItem));

        assertThrows(BadRequestException.class, () -> staffOrderService.createOrder(request));
    }

    @Test
    void testUpdateOrderStatus_InvalidTransition_Fails() {
        StaffUpdateOrderStatusRequest request = new StaffUpdateOrderStatusRequest();
        request.setStatus(OrderStatus.COMPLETED); // CONFIRMED -> COMPLETED is invalid for staff

        when(orderRepository.findById(1000L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> staffOrderService.updateOrderStatus(1000L, request));
    }
}
