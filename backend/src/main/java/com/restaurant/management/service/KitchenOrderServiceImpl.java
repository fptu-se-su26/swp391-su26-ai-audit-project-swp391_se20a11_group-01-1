package com.restaurant.management.service;

import com.restaurant.management.dto.kitchen.KitchenOrderResponse;
import com.restaurant.management.dto.kitchen.KitchenUpdateOrderStatusRequest;
import com.restaurant.management.dto.order.OrderItemResponse;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitchenOrderServiceImpl implements KitchenOrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KitchenOrderResponse> getActiveOrders() {
        List<OrderStatus> activeStatuses = Arrays.asList(OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY);
        return orderRepository.findByOrderStatusInOrderByCreatedAtAsc(activeStatuses).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public KitchenOrderResponse getOrderById(Long id) {
        RestaurantOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public KitchenOrderResponse updateOrderStatus(Long id, KitchenUpdateOrderStatusRequest request) {
        RestaurantOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus current = order.getOrderStatus();
        OrderStatus target = request.getStatus();

        // Valid transitions for kitchen:
        // CONFIRMED -> PREPARING
        // PREPARING -> READY
        boolean valid = false;
        if (current == OrderStatus.CONFIRMED && target == OrderStatus.PREPARING) valid = true;
        if (current == OrderStatus.PREPARING && target == OrderStatus.READY) valid = true;

        if (!valid) {
            throw new BadRequestException("Invalid status transition for kitchen from " + current + " to " + target);
        }

        order.setOrderStatus(target);
        return mapToResponse(orderRepository.save(order));
    }

    private KitchenOrderResponse mapToResponse(RestaurantOrder order) {
        return KitchenOrderResponse.builder()
                .id(order.getId())
                .tableId(order.getTableId())
                .orderStatus(order.getOrderStatus())
                .orderType(order.getOrderType())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .foodItemId(item.getFoodItem().getId())
                        .foodName(item.getFoodItem().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .note(item.getNote())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
