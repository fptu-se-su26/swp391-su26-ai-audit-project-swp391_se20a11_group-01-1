package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.UpdateOrderItemStatusRequest;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import com.rms.restaurant_management_system.repository.OrderItemRepository;
import com.rms.restaurant_management_system.service.impl.KitchenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KitchenServiceImplTest {
    @Mock OrderItemRepository repository;
    KitchenServiceImpl service;

    @BeforeEach void setUp() { service = new KitchenServiceImpl(repository); }

    @Test
    void activeQueueQueriesOnlyPendingAndPreparingAndMapsOrderContext() {
        Order order = Order.builder().orderId(11L).orderCode("ORD-11").tableName("T1").note("No onion").build();
        OrderItem item = OrderItem.builder().orderItemId(2L).foodId(8L).foodName("Soup")
                .quantity(2).status(OrderItemStatus.PREPARING).order(order).build();
        when(repository.findByStatusIn(anyList())).thenReturn(List.of(item));
        var result = service.getActiveKitchenItems();
        ArgumentCaptor<List<OrderItemStatus>> statuses = ArgumentCaptor.forClass(List.class);
        verify(repository).findByStatusIn(statuses.capture());
        assertEquals(List.of(OrderItemStatus.PENDING, OrderItemStatus.PREPARING), statuses.getValue());
        assertAll(
                () -> assertEquals("ORD-11", result.getFirst().getOrderCode()),
                () -> assertEquals("T1", result.getFirst().getTableName()),
                () -> assertEquals("No onion", result.getFirst().getNote())
        );
    }

    @Test
    void updatePersistsRequestedStatus() {
        OrderItem item = OrderItem.builder().orderItemId(2L).status(OrderItemStatus.PENDING).build();
        UpdateOrderItemStatusRequest request = new UpdateOrderItemStatusRequest();
        request.setStatus(OrderItemStatus.READY);
        when(repository.findById(2L)).thenReturn(Optional.of(item));
        service.updateItemStatus(2L, request);
        assertEquals(OrderItemStatus.READY, item.getStatus());
        verify(repository).save(item);
    }

    @Test
    void updateMissingItemDoesNotWrite() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.updateItemStatus(99L, new UpdateOrderItemStatusRequest()));
        verify(repository, never()).save(any());
    }
}
