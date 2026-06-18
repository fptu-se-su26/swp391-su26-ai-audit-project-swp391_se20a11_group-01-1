package com.restaurant.management.service;

import com.restaurant.management.dto.kitchen.KitchenOrderResponse;
import com.restaurant.management.dto.kitchen.KitchenUpdateOrderStatusRequest;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KitchenOrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private KitchenOrderServiceImpl kitchenOrderService;

    private RestaurantOrder order;

    @BeforeEach
    void setUp() {
        order = new RestaurantOrder();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setItems(new ArrayList<>());
    }

    @Test
    void testGetActiveOrders() {
        List<OrderStatus> activeStatuses = Arrays.asList(OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY);
        when(orderRepository.findByOrderStatusInOrderByCreatedAtAsc(activeStatuses)).thenReturn(Arrays.asList(order));

        List<KitchenOrderResponse> responses = kitchenOrderService.getActiveOrders();
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
    }

    @Test
    void testUpdateOrderStatus_ConfirmedToPreparing_Success() {
        KitchenUpdateOrderStatusRequest request = new KitchenUpdateOrderStatusRequest();
        request.setStatus(OrderStatus.PREPARING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(RestaurantOrder.class))).thenReturn(order);

        KitchenOrderResponse response = kitchenOrderService.updateOrderStatus(1L, request);

        assertEquals(OrderStatus.PREPARING, response.getOrderStatus());
    }

    @Test
    void testUpdateOrderStatus_PreparingToReady_Success() {
        order.setOrderStatus(OrderStatus.PREPARING);
        
        KitchenUpdateOrderStatusRequest request = new KitchenUpdateOrderStatusRequest();
        request.setStatus(OrderStatus.READY);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(RestaurantOrder.class))).thenReturn(order);

        KitchenOrderResponse response = kitchenOrderService.updateOrderStatus(1L, request);

        assertEquals(OrderStatus.READY, response.getOrderStatus());
    }

    @Test
    void testUpdateOrderStatus_KitchenCannotComplete() {
        KitchenUpdateOrderStatusRequest request = new KitchenUpdateOrderStatusRequest();
        request.setStatus(OrderStatus.COMPLETED); // Kitchen not allowed

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> kitchenOrderService.updateOrderStatus(1L, request));
    }
}
