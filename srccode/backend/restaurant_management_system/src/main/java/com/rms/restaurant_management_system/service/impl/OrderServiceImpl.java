package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.OrderItemRequest;
import com.rms.restaurant_management_system.dto.request.OrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.OrderItemResponse;
import com.rms.restaurant_management_system.dto.response.OrderResponse;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .user(user)
                .status(OrderStatus.PENDING)
                .note(request.getNote())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Food food = foodRepository.findById(itemRequest.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Food not found: " + itemRequest.getFoodId()));

            if (!food.getIsAvailable()) {
                throw new RuntimeException("Food is not available: " + food.getFoodName());
            }

            BigDecimal unitPrice = food.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .foodId(food.getFoodId())
                    .foodName(food.getFoodName())
                    .unitPrice(unitPrice)
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .imageUrl(food.getImageUrl())
                    .emoji(food.getEmoji())
                    .build();

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Long userId) {
        return orderRepository.findByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(String status) {
        OrderStatus orderStatus;

        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("Invalid order status: " + status);
        }

        return orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Completed order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cancelled order cannot be updated");
        }

        if (currentStatus == OrderStatus.COMPLETED) {
            throw new RuntimeException("Completed order cannot be updated");
        }

        if (currentStatus == newStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY || newStatus == OrderStatus.CANCELLED;
            case READY -> newStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!valid) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getOrderItemId(),
                        item.getFoodId(),
                        item.getFoodName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal(),
                        item.getImageUrl(),
                        item.getEmoji()
                ))
                .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderCode(),
                order.getUser().getUserId(),
                order.getUser().getUsername(),
                order.getUser().getEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getNote(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }

    private String generateOrderCode() {
        String timestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        int random = (int) (Math.random() * 9000) + 1000;

        return "ORD-" + timestamp + "-" + random;
    }
}