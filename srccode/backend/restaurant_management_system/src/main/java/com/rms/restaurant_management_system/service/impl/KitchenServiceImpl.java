package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.UpdateOrderItemStatusRequest;
import com.rms.restaurant_management_system.dto.response.KitchenItemResponse;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import com.rms.restaurant_management_system.repository.OrderItemRepository;
import com.rms.restaurant_management_system.service.interfaces.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitchenServiceImpl implements KitchenService {

    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KitchenItemResponse> getActiveKitchenItems() {
        List<OrderItemStatus> activeStatuses = Arrays.asList(OrderItemStatus.PENDING, OrderItemStatus.PREPARING);
        List<OrderItem> items = orderItemRepository.findByStatusIn(activeStatuses);

        return items.stream().map(item -> KitchenItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .orderId(item.getOrder().getOrderId())
                .orderCode(item.getOrder().getOrderCode())
                .tableName(item.getOrder().getTableName())
                .foodId(item.getFoodId())
                .foodName(item.getFoodName())
                .quantity(item.getQuantity())
                .note(item.getOrder().getNote()) // Sharing the main order note for now
                .status(item.getStatus())
                .orderCreatedAt(item.getOrder().getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateItemStatus(Long orderItemId, UpdateOrderItemStatusRequest request) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        item.setStatus(request.getStatus());
        orderItemRepository.save(item);
    }
}
