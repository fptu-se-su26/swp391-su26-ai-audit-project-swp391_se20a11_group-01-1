package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.OrderItemRequest;
import com.rms.restaurant_management_system.dto.request.OrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderItemStatusRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.KitchenItemResponse;
import com.rms.restaurant_management_system.dto.response.OrderResponse;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.repository.OrderItemRepository;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.PaymentRepository;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"CONFIRMED", "PREPARING"})
    void appendingIsAllowedForConfirmedAndPreparingOrders(OrderStatus orderStatus) {
        RestaurantTable table = occupiedTable();
        OrderItemStatus existingStatus = orderStatus == OrderStatus.CONFIRMED
                ? OrderItemStatus.CONFIRMED
                : OrderItemStatus.PREPARING;
        OrderItem existingItem = item(11L, existingStatus, "10000");
        Order order = order(1L, orderStatus, existingItem);
        order.setTableId(9L);
        order.setTableName("T9");
        Food food = food(22L, "20000");
        OrderRequest request = appendRequest(22L);

        when(restaurantTableRepository.findByTableIdForUpdate(9L))
                .thenReturn(Optional.of(table));
        when(orderRepository.findByOrderCode("ORD-1")).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderOrderId(1L)).thenReturn(Optional.empty());
        when(foodRepository.findById(22L)).thenReturn(Optional.of(food));
        when(orderRepository.save(order)).thenReturn(order);

        OrderResponse response = orderService.createOrder(request);

        assertEquals(1L, response.getOrderId());
        assertEquals(9L, response.getTableId());
        assertEquals(2, order.getItems().size());
        assertEquals(existingStatus, existingItem.getStatus());
        assertEquals(OrderItemStatus.CONFIRMED, order.getItems().get(1).getStatus());
        assertEquals(orderStatus, order.getStatus());
        assertEquals(new BigDecimal("30000"), order.getTotalAmount());
        verify(restaurantTableRepository, never()).save(table);
    }

    @Test
    void appendingToReadyOrderKeepsOldStatusAndCreatesConfirmedItem() {
        RestaurantTable table = RestaurantTable.builder()
                .tableId(9L)
                .tableName("T9")
                .capacity(4)
                .status(TableStatus.OCCUPIED)
                .currentOrderCode("ORD-1")
                .build();
        OrderItem readyItem = item(11L, OrderItemStatus.READY, "10000");
        Order order = order(1L, OrderStatus.READY, readyItem);
        order.setTableId(9L);
        order.setTableName("T9");
        Food food = Food.builder()
                .foodId(22L)
                .foodName("Soup")
                .price(new BigDecimal("20000"))
                .imageUrl("soup.jpg")
                .emoji("S")
                .build();
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setFoodId(22L);
        itemRequest.setQuantity(1);
        itemRequest.setNote("No pepper");
        OrderRequest request = new OrderRequest();
        request.setTableId(9L);
        request.setItems(List.of(itemRequest));

        when(restaurantTableRepository.findByTableIdForUpdate(9L))
                .thenReturn(Optional.of(table));
        when(orderRepository.findByOrderCode("ORD-1")).thenReturn(Optional.of(order));
        when(foodRepository.findById(22L)).thenReturn(Optional.of(food));
        when(paymentRepository.findByOrderOrderId(1L)).thenReturn(Optional.empty());
        when(orderRepository.save(order)).thenReturn(order);

        OrderResponse response = orderService.createOrder(request);

        assertEquals(1L, response.getOrderId());
        assertEquals("ORD-1", response.getOrderCode());
        assertEquals(9L, response.getTableId());
        assertEquals(2, order.getItems().size());
        assertEquals(OrderItemStatus.READY, order.getItems().get(0).getStatus());
        assertEquals(OrderItemStatus.CONFIRMED, order.getItems().get(1).getStatus());
        assertEquals("No pepper", order.getItems().get(1).getNote());
        assertEquals(OrderStatus.PREPARING, order.getStatus());
        assertEquals(new BigDecimal("30000"), order.getTotalAmount());
        assertEquals(TableStatus.OCCUPIED, table.getStatus());
        assertEquals("ORD-1", table.getCurrentOrderCode());
        verify(restaurantTableRepository, never()).save(table);
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"PENDING", "PAID"})
    void appendingAfterPaymentStartedIsRejectedWithoutCreatingItems(
            PaymentStatus paymentStatus
    ) {
        RestaurantTable table = RestaurantTable.builder()
                .tableId(9L)
                .tableName("T9")
                .capacity(4)
                .status(TableStatus.OCCUPIED)
                .currentOrderCode("ORD-1")
                .build();
        OrderItem readyItem = item(11L, OrderItemStatus.READY, "10000");
        Order order = order(1L, OrderStatus.READY, readyItem);
        Payment pendingPayment = Payment.builder()
                .order(order)
                .status(paymentStatus)
                .amount(new BigDecimal("10000"))
                .build();
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setFoodId(22L);
        itemRequest.setQuantity(1);
        OrderRequest request = new OrderRequest();
        request.setTableId(9L);
        request.setItems(List.of(itemRequest));

        when(restaurantTableRepository.findByTableIdForUpdate(9L))
                .thenReturn(Optional.of(table));
        when(orderRepository.findByOrderCode("ORD-1")).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderOrderId(1L))
                .thenReturn(Optional.of(pendingPayment));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(request)
        );

        assertEquals("Cannot add items after payment has started.", exception.getMessage());
        assertEquals(1, order.getItems().size());
        verify(foodRepository, never()).findById(22L);
    }

    @Test
    void confirmedItemCanMoveToPreparingAndMakesOrderPreparing() {
        OrderItem target = item(11L, OrderItemStatus.CONFIRMED, "10000");
        OrderItem sibling = item(12L, OrderItemStatus.CONFIRMED, "20000");
        Order order = order(1L, OrderStatus.CONFIRMED, target, sibling);
        arrangeLockedOrder(target, order);
        UpdateOrderItemStatusRequest request = statusRequest(OrderItemStatus.PREPARING);

        KitchenItemResponse response = orderService.updateOrderItemStatus(11L, request);

        assertEquals(OrderItemStatus.PREPARING, response.getStatus());
        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void preparingItemCannotBeCancelled() {
        OrderItem target = item(11L, OrderItemStatus.PREPARING, "10000");
        Order order = order(1L, OrderStatus.PREPARING, target);
        arrangeLockedOrderWithoutSave(target, order);
        UpdateOrderItemStatusRequest request = statusRequest(OrderItemStatus.CANCELLED);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.updateOrderItemStatus(11L, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("PREPARING -> CANCELLED"));
        verify(orderRepository, never()).save(order);
    }

    @Test
    void emptyKitchenStatusesAreRejectedAsBadRequest() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.getKitchenItems(List.of())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(
                "At least one order item status is required",
                exception.getReason()
        );
    }

    @Test
    void allNonCancelledItemsReadyMakesOrderReady() {
        OrderItem target = item(11L, OrderItemStatus.PREPARING, "10000");
        OrderItem readySibling = item(12L, OrderItemStatus.READY, "20000");
        OrderItem cancelledSibling = item(13L, OrderItemStatus.CANCELLED, "30000");
        Order order = order(
                1L,
                OrderStatus.PREPARING,
                target,
                readySibling,
                cancelledSibling
        );
        arrangeLockedOrder(target, order);

        orderService.updateOrderItemStatus(11L, statusRequest(OrderItemStatus.READY));

        assertEquals(OrderStatus.READY, order.getStatus());
    }

    @Test
    void cancellingLastConfirmedItemCancelsOrderAndRemovesItFromTotal() {
        OrderItem target = item(11L, OrderItemStatus.CONFIRMED, "10000");
        Order order = order(1L, OrderStatus.CONFIRMED, target);
        arrangeLockedOrder(target, order);

        orderService.updateOrderItemStatus(11L, statusRequest(OrderItemStatus.CANCELLED));

        assertEquals(OrderItemStatus.CANCELLED, target.getStatus());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
    }

    @Test
    void cancellingWholeOrderCancelsEveryItemAndSetsTotalToZero() {
        OrderItem firstItem = item(11L, OrderItemStatus.CONFIRMED, "10000");
        OrderItem secondItem = item(12L, OrderItemStatus.CONFIRMED, "20000");
        Order order = order(1L, OrderStatus.CONFIRMED, firstItem, secondItem);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderId(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderOrderId(1L)).thenReturn(Optional.empty());
        when(orderRepository.save(order)).thenReturn(order);

        orderService.cancelOrder(1L);

        assertEquals(OrderItemStatus.CANCELLED, firstItem.getStatus());
        assertEquals(OrderItemStatus.CANCELLED, secondItem.getStatus());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
    }

    @Test
    void paidPaymentTakesPrecedenceDuringRecalculation() {
        OrderItem target = item(11L, OrderItemStatus.PREPARING, "10000");
        Order order = order(1L, OrderStatus.PREPARING, target);
        Payment paidPayment = Payment.builder()
                .order(order)
                .status(PaymentStatus.PAID)
                .amount(new BigDecimal("10000"))
                .build();

        when(orderItemRepository.findOrderIdByOrderItemId(11L))
                .thenReturn(Optional.of(1L));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderId(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderOrderId(1L))
                .thenReturn(Optional.of(paidPayment));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.updateOrderItemStatus(11L, statusRequest(OrderItemStatus.READY));

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void unpaidOrderCannotBeCompletedDirectly() {
        OrderItem readyItem = item(11L, OrderItemStatus.READY, "10000");
        Order order = order(1L, OrderStatus.READY, readyItem);
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderId(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderOrderId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.updateOrderStatus(1L, request)
        );

        assertTrue(exception.getMessage().contains("only be completed after payment"));
        verify(orderRepository, never()).save(order);
    }

    private void arrangeLockedOrder(OrderItem target, Order order) {
        arrangeLockedOrderWithoutSave(target, order);
        when(paymentRepository.findByOrderOrderId(order.getOrderId()))
                .thenReturn(Optional.empty());
        when(orderRepository.save(order)).thenReturn(order);
    }

    private void arrangeLockedOrderWithoutSave(OrderItem target, Order order) {
        when(orderItemRepository.findOrderIdByOrderItemId(target.getOrderItemId()))
                .thenReturn(Optional.of(order.getOrderId()));
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderId(order.getOrderId())).thenReturn(Optional.of(order));
    }

    private UpdateOrderItemStatusRequest statusRequest(OrderItemStatus status) {
        UpdateOrderItemStatusRequest request = new UpdateOrderItemStatusRequest();
        request.setStatus(status);
        return request;
    }

    private RestaurantTable occupiedTable() {
        return RestaurantTable.builder()
                .tableId(9L)
                .tableName("T9")
                .capacity(4)
                .status(TableStatus.OCCUPIED)
                .currentOrderCode("ORD-1")
                .isActive(true)
                .build();
    }

    private Food food(Long foodId, String price) {
        return Food.builder()
                .foodId(foodId)
                .foodName("Food " + foodId)
                .price(new BigDecimal(price))
                .imageUrl("food.jpg")
                .emoji("F")
                .build();
    }

    private OrderRequest appendRequest(Long foodId) {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setFoodId(foodId);
        itemRequest.setQuantity(1);

        OrderRequest request = new OrderRequest();
        request.setTableId(9L);
        request.setItems(List.of(itemRequest));
        return request;
    }

    private Order order(Long orderId, OrderStatus status, OrderItem... items) {
        Order order = Order.builder()
                .orderId(orderId)
                .orderCode("ORD-1")
                .status(status)
                .totalAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
            order.setTotalAmount(order.getTotalAmount().add(item.getSubtotal()));
        }

        return order;
    }

    private OrderItem item(Long id, OrderItemStatus status, String subtotal) {
        return OrderItem.builder()
                .orderItemId(id)
                .foodId(id)
                .foodName("Food " + id)
                .unitPrice(new BigDecimal(subtotal))
                .quantity(1)
                .subtotal(new BigDecimal(subtotal))
                .status(status)
                .createdAt(LocalDateTime.now())
                .statusUpdatedAt(LocalDateTime.now())
                .build();
    }
}
