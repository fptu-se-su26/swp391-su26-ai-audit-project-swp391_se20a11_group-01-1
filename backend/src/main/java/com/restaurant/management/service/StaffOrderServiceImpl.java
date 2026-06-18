package com.restaurant.management.service;

import com.restaurant.management.dto.order.OrderItemResponse;
import com.restaurant.management.dto.staff.StaffCreateOrderRequest;
import com.restaurant.management.dto.staff.StaffOrderItemRequest;
import com.restaurant.management.dto.staff.StaffOrderResponse;
import com.restaurant.management.dto.staff.StaffUpdateOrderStatusRequest;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.entity.order.OrderItem;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.OrderType;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.entity.reservation.RestaurantTable;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.FoodItemRepository;
import com.restaurant.management.repository.OrderRepository;
import com.restaurant.management.repository.RestaurantTableRepository;
import com.restaurant.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffOrderServiceImpl implements StaffOrderService {

    private final OrderRepository orderRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;

    @Override
    @Transactional
    public StaffOrderResponse createOrder(StaffCreateOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User staffUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long tableId = null;
        if (request.getOrderType() == OrderType.DINE_IN) {
            if (request.getTableId() != null) {
                RestaurantTable table = tableRepository.findById(request.getTableId())
                        .filter(t -> t.getDeletedAt() == null)
                        .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
                tableId = table.getId();
            }
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (StaffOrderItemRequest itemReq : request.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(itemReq.getFoodItemId())
                    .filter(f -> f.getDeletedAt() == null && Boolean.TRUE.equals(f.getIsAvailable()))
                    .orElseThrow(() -> new BadRequestException("Food item not available or not found: " + itemReq.getFoodItemId()));

            BigDecimal itemTotal = foodItem.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            subTotal = subTotal.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .foodItem(foodItem)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(foodItem.getPrice()) // Snapshot price
                    .note(itemReq.getNote())
                    .build();
            items.add(orderItem);
        }

        RestaurantOrder order = RestaurantOrder.builder()
                .customer(staffUser) // Use staff as customer for counter orders
                .tableId(tableId)
                .orderStatus(OrderStatus.CONFIRMED) // Staff order is confirmed immediately
                .orderType(request.getOrderType())
                .subTotal(subTotal)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(subTotal)
                .note(request.getNote())
                .build();

        for (OrderItem item : items) {
            order.addItem(item);
        }

        RestaurantOrder savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffOrderResponse> getAllOrders() {
        return orderRepository.findAll().stream() // Assume we get all orders for staff
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StaffOrderResponse getOrderById(Long id) {
        RestaurantOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public StaffOrderResponse updateOrderStatus(Long id, StaffUpdateOrderStatusRequest request) {
        RestaurantOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus current = order.getOrderStatus();
        OrderStatus target = request.getStatus();

        // Valid transitions for staff:
        // CONFIRMED -> PREPARING
        // READY -> COMPLETED
        // CONFIRMED -> CANCELLED
        boolean valid = false;
        if (current == OrderStatus.CONFIRMED && target == OrderStatus.PREPARING) valid = true;
        if (current == OrderStatus.READY && target == OrderStatus.COMPLETED) valid = true;
        if (current == OrderStatus.CONFIRMED && target == OrderStatus.CANCELLED) valid = true;

        if (!valid) {
            throw new BadRequestException("Invalid status transition from " + current + " to " + target);
        }

        order.setOrderStatus(target);
        return mapToResponse(orderRepository.save(order));
    }

    private StaffOrderResponse mapToResponse(RestaurantOrder order) {
        return StaffOrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .tableId(order.getTableId())
                .orderStatus(order.getOrderStatus())
                .orderType(order.getOrderType())
                .subTotal(order.getSubTotal())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
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
