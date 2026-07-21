package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.TransferTableRequest;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantTableServiceImplTest {

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private RestaurantTableServiceImpl tableService;

    @Test
    void transferTableIsRejectedWhenAnyItemIsPreparing() {
        RestaurantTable source = sourceTable();
        RestaurantTable target = targetTable();
        Order order = order(
                OrderStatus.PREPARING,
                item(11L, OrderItemStatus.READY),
                item(12L, OrderItemStatus.PREPARING)
        );
        arrangeTransfer(source, target, order);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> tableService.transferTable(1L, transferRequest())
        );

        assertEquals(
                "Cannot transfer table while kitchen preparation is in progress.",
                exception.getMessage()
        );
        verify(orderRepository, never()).save(order);
        verify(tableRepository, never()).save(source);
        verify(tableRepository, never()).save(target);
    }

    @Test
    void transferTableAllowsConfirmedAndReadyItemsAndPreservesStatuses() {
        RestaurantTable source = sourceTable();
        RestaurantTable target = targetTable();
        OrderItem confirmedItem = item(11L, OrderItemStatus.CONFIRMED);
        OrderItem readyItem = item(12L, OrderItemStatus.READY);
        Order order = order(OrderStatus.PREPARING, confirmedItem, readyItem);
        arrangeTransfer(source, target, order);
        when(tableRepository.findByIsActiveTrueOrderByTableIdAsc())
                .thenReturn(List.of(source, target));

        tableService.transferTable(1L, transferRequest());

        assertEquals(2L, order.getTableId());
        assertEquals("T2", order.getTableName());
        assertEquals(OrderItemStatus.CONFIRMED, confirmedItem.getStatus());
        assertEquals(OrderItemStatus.READY, readyItem.getStatus());
        assertEquals(TableStatus.EMPTY, source.getStatus());
        assertNull(source.getCurrentOrderCode());
        assertEquals(TableStatus.OCCUPIED, target.getStatus());
        assertEquals("ORD-1", target.getCurrentOrderCode());
        verify(orderRepository).save(order);
        verify(tableRepository).save(source);
        verify(tableRepository).save(target);
    }

    @Test
    void transferTableAllowsAnOrderWhoseItemsAreAllReady() {
        RestaurantTable source = sourceTable();
        RestaurantTable target = targetTable();
        Order order = order(
                OrderStatus.READY,
                item(11L, OrderItemStatus.READY),
                item(12L, OrderItemStatus.READY)
        );
        arrangeTransfer(source, target, order);
        when(tableRepository.findByIsActiveTrueOrderByTableIdAsc())
                .thenReturn(List.of(source, target));

        tableService.transferTable(1L, transferRequest());

        assertEquals(2L, order.getTableId());
        assertEquals(OrderItemStatus.READY, order.getItems().get(0).getStatus());
        assertEquals(OrderItemStatus.READY, order.getItems().get(1).getStatus());
    }

    private void arrangeTransfer(
            RestaurantTable source,
            RestaurantTable target,
            Order order
    ) {
        when(tableRepository.findByTableIdForUpdate(1L)).thenReturn(Optional.of(source));
        when(tableRepository.findByTableIdForUpdate(2L)).thenReturn(Optional.of(target));
        when(orderRepository.findByOrderCode("ORD-1")).thenReturn(Optional.of(order));
    }

    private TransferTableRequest transferRequest() {
        TransferTableRequest request = new TransferTableRequest();
        request.setTargetTableId(2L);
        return request;
    }

    private RestaurantTable sourceTable() {
        return RestaurantTable.builder()
                .tableId(1L)
                .tableName("T1")
                .capacity(4)
                .status(TableStatus.OCCUPIED)
                .currentOrderCode("ORD-1")
                .reservedBy("Guest")
                .isActive(true)
                .build();
    }

    private RestaurantTable targetTable() {
        return RestaurantTable.builder()
                .tableId(2L)
                .tableName("T2")
                .capacity(4)
                .status(TableStatus.EMPTY)
                .isActive(true)
                .build();
    }

    private Order order(OrderStatus status, OrderItem... items) {
        Order order = Order.builder()
                .orderId(1L)
                .orderCode("ORD-1")
                .tableId(1L)
                .tableName("T1")
                .status(status)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        return order;
    }

    private OrderItem item(Long id, OrderItemStatus status) {
        return OrderItem.builder()
                .orderItemId(id)
                .foodId(id)
                .foodName("Food " + id)
                .unitPrice(BigDecimal.TEN)
                .quantity(1)
                .subtotal(BigDecimal.TEN)
                .status(status)
                .build();
    }
}
