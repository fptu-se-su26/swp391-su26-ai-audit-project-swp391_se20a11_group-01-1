package com.restaurant.service;

import com.restaurant.dto.kitchen.*;
import com.restaurant.entity.*;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.*;
import com.restaurant.repository.*;
import com.restaurant.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KitchenService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KitchenTaskRepository kitchenTaskRepository;
    private final CurrentUserService currentUserService;

    public List<KitchenOrderResponse> getCookingOrders(String status) {
        List<Order> orders;
        if (status == null || status.isBlank()) {
            orders = orderRepository.findAllByOrderByCreatedAtDesc().stream()
                    .filter(o -> o.getOrderStatus() == OrderStatus.CONFIRMED || o.getOrderStatus() == OrderStatus.PREPARING || o.getOrderStatus() == OrderStatus.READY)
                    .toList();
        } else {
            orders = orderRepository.findByOrderStatus(parseOrderStatus(status));
        }
        return orders.stream().map(this::toKitchenOrderResponse).toList();
    }

    public KitchenOrderResponse getCookingOrderDetail(Long orderId) {
        return toKitchenOrderResponse(getOrder(orderId));
    }

    public List<KitchenOrderItemResponse> getItemsByStatus(String status) {
        OrderItemStatus itemStatus = status == null || status.isBlank() ? OrderItemStatus.PENDING : parseItemStatus(status);
        return orderItemRepository.findByItemStatus(itemStatus).stream()
                .filter(item -> item.getOrder().getOrderStatus() != OrderStatus.CANCELLED && item.getOrder().getOrderStatus() != OrderStatus.COMPLETED)
                .sorted(Comparator.comparing(item -> item.getOrder().getCreatedAt()))
                .map(this::toKitchenItemResponse)
                .toList();
    }

    @Transactional
    public KitchenTaskResponse updateOrderItemStatus(Long orderItemId, UpdateKitchenItemStatusRequest request) {
        OrderItem item = getOrderItem(orderItemId);
        Order order = item.getOrder();
        if (order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot update kitchen item of a " + order.getOrderStatus() + " order");
        }

        OrderItemStatus nextStatus = parseItemStatus(request.getItemStatus());
        validateItemTransition(item.getItemStatus(), nextStatus);
        item.setItemStatus(nextStatus);
        if (request.getNote() != null && !request.getNote().isBlank()) item.setKitchenNote(request.getNote());
        orderItemRepository.save(item);

        updateParentOrderStatus(order);
        orderRepository.save(order);

        KitchenTask task = upsertKitchenTask(item, nextStatus, request.getNote());
        return toTaskResponse(kitchenTaskRepository.save(task));
    }

    @Transactional
    public KitchenTaskResponse acceptOrderItem(Long orderItemId) {
        UpdateKitchenItemStatusRequest request = new UpdateKitchenItemStatusRequest();
        request.setItemStatus(OrderItemStatus.PREPARING.name());
        request.setNote("Accepted by kitchen");
        return updateOrderItemStatus(orderItemId, request);
    }

    @Transactional
    public KitchenTaskResponse rejectOrderItem(Long orderItemId, UpdateKitchenItemStatusRequest request) {
        if (request.getNote() == null || request.getNote().isBlank()) {
            throw new BadRequestException("Reject reason is required");
        }
        request.setItemStatus(OrderItemStatus.REJECTED.name());
        return updateOrderItemStatus(orderItemId, request);
    }

    private KitchenTask upsertKitchenTask(OrderItem item, OrderItemStatus nextStatus, String note) {
        User kitchenUser = currentUserService.getCurrentUser();
        KitchenTask task = kitchenTaskRepository.findTopByOrderItem_OrderItemIdOrderByKitchenTaskIdDesc(item.getOrderItemId())
                .orElseGet(() -> KitchenTask.builder().orderItem(item).kitchenUser(kitchenUser).taskStatus(KitchenTaskStatus.WAITING).build());
        task.setKitchenUser(kitchenUser);
        task.setTaskStatus(toKitchenTaskStatus(nextStatus));
        task.setNote(note);
        if (nextStatus == OrderItemStatus.PREPARING && task.getStartedAt() == null) task.setStartedAt(LocalDateTime.now());
        if (nextStatus == OrderItemStatus.READY || nextStatus == OrderItemStatus.REJECTED || nextStatus == OrderItemStatus.SERVED) task.setFinishedAt(LocalDateTime.now());
        return task;
    }

    private void updateParentOrderStatus(Order order) {
        List<OrderItem> items = order.getItems();
        boolean anyPreparing = items.stream().anyMatch(i -> i.getItemStatus() == OrderItemStatus.PREPARING);
        boolean allReadyOrFinal = items.stream().allMatch(i -> i.getItemStatus() == OrderItemStatus.READY || i.getItemStatus() == OrderItemStatus.SERVED || i.getItemStatus() == OrderItemStatus.REJECTED || i.getItemStatus() == OrderItemStatus.CANCELLED);
        if (anyPreparing) order.setOrderStatus(OrderStatus.PREPARING);
        if (allReadyOrFinal) order.setOrderStatus(OrderStatus.READY);
    }

    private void validateItemTransition(OrderItemStatus current, OrderItemStatus next) {
        if (current == OrderItemStatus.SERVED || current == OrderItemStatus.CANCELLED) {
            throw new BadRequestException("Cannot update an item that is already " + current);
        }
        if (current == OrderItemStatus.READY && next == OrderItemStatus.PREPARING) {
            throw new BadRequestException("Cannot move READY item back to PREPARING");
        }
    }

    private KitchenTaskStatus toKitchenTaskStatus(OrderItemStatus status) {
        return switch (status) {
            case PREPARING -> KitchenTaskStatus.PREPARING;
            case READY -> KitchenTaskStatus.READY;
            case REJECTED -> KitchenTaskStatus.REJECTED;
            case SERVED -> KitchenTaskStatus.COMPLETED;
            default -> KitchenTaskStatus.WAITING;
        };
    }

    private KitchenOrderResponse toKitchenOrderResponse(Order order) {
        return KitchenOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus().name())
                .orderType(order.getOrderType().name())
                .customerName(order.getCustomer() == null ? null : order.getCustomer().getFullName())
                .staffName(order.getStaff() == null ? null : order.getStaff().getFullName())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(this::toKitchenItemResponse).toList())
                .build();
    }

    private KitchenOrderItemResponse toKitchenItemResponse(OrderItem item) {
        return KitchenOrderItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .orderId(item.getOrder().getOrderId())
                .foodId(item.getFood().getFoodId())
                .foodName(item.getFood().getFoodName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .itemStatus(item.getItemStatus().name())
                .kitchenNote(item.getKitchenNote())
                .estimatedCookingTime(item.getFood().getEstimatedCookingTime())
                .build();
    }

    private KitchenTaskResponse toTaskResponse(KitchenTask task) {
        OrderItem item = task.getOrderItem();
        return KitchenTaskResponse.builder()
                .kitchenTaskId(task.getKitchenTaskId())
                .orderItemId(item.getOrderItemId())
                .orderId(item.getOrder().getOrderId())
                .foodName(item.getFood().getFoodName())
                .taskStatus(task.getTaskStatus().name())
                .itemStatus(item.getItemStatus().name())
                .kitchenUserName(task.getKitchenUser() == null ? null : task.getKitchenUser().getFullName())
                .startedAt(task.getStartedAt())
                .finishedAt(task.getFinishedAt())
                .note(task.getNote())
                .build();
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
    private OrderItem getOrderItem(Long orderItemId) {
        return orderItemRepository.findById(orderItemId).orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
    }
    private OrderStatus parseOrderStatus(String value) {
        try { return OrderStatus.valueOf(value.trim().toUpperCase()); }
        catch (Exception e) { throw new BadRequestException("Invalid order status: " + value); }
    }
    private OrderItemStatus parseItemStatus(String value) {
        try { return OrderItemStatus.valueOf(value.trim().toUpperCase()); }
        catch (Exception e) { throw new BadRequestException("Invalid item status: " + value); }
    }
}
