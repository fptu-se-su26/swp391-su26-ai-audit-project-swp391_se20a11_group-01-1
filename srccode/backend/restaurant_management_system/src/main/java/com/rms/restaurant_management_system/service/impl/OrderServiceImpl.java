package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.OrderItemRequest;
import com.rms.restaurant_management_system.dto.request.OrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderItemStatusRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.KitchenItemResponse;
import com.rms.restaurant_management_system.dto.response.OrderItemResponse;
import com.rms.restaurant_management_system.dto.response.OrderResponse;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.entity.User;
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
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = null;

        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        RestaurantTable table = null;

        if (request.getTableId() != null) {
            table = restaurantTableRepository.findByTableIdForUpdate(request.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));

            if (table.getIsActive() == null || !table.getIsActive()) {
                throw new RuntimeException("Table is inactive");
            }
        }

        Order activeOrder = findAppendableActiveOrder(table);

        if (activeOrder != null) {
            appendItems(activeOrder, request.getItems(), request.getNote());
            recalculateTotalAmount(activeOrder);
            recalculateOrderStatus(activeOrder);
            return mapToResponse(orderRepository.save(activeOrder));
        }

        String customerName = request.getCustomerName();

        if ((customerName == null || customerName.isBlank()) && user != null) {
            customerName = user.getUsername();
        }

        if ((customerName == null || customerName.isBlank()) && table != null) {
            customerName = "Khách " + table.getTableName();
        }

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .user(user)
                .tableId(table != null ? table.getTableId() : null)
                .tableName(table != null ? table.getTableName() : null)
                .customerName(customerName)
                .customerPhone(request.getCustomerPhone())
                .status(OrderStatus.CONFIRMED)
                .note(request.getNote())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        appendItems(order, request.getItems(), request.getNote());
        recalculateTotalAmount(order);
        recalculateOrderStatus(order);

        Order savedOrder = orderRepository.save(order);

        if (table != null) {
            table.setStatus(TableStatus.OCCUPIED);
            table.setCurrentOrderCode(savedOrder.getOrderCode());
            table.setReservedBy(customerName);
            restaurantTableRepository.save(table);
        }

        return mapToResponse(savedOrder);
    }

    private Order findAppendableActiveOrder(RestaurantTable table) {
        if (table == null
                || table.getCurrentOrderCode() == null
                || table.getCurrentOrderCode().isBlank()) {
            return null;
        }

        Order activeOrder = orderRepository
                .findByOrderCode(table.getCurrentOrderCode().trim())
                .orElse(null);

        if (activeOrder == null) {
            return null;
        }

        if (activeOrder.getStatus() == OrderStatus.COMPLETED
                || activeOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException(
                    "Cannot add items to a completed or cancelled order"
            );
        }

        if (activeOrder.getStatus() != OrderStatus.CONFIRMED
                && activeOrder.getStatus() != OrderStatus.PREPARING
                && activeOrder.getStatus() != OrderStatus.READY) {
            throw new RuntimeException(
                    "Cannot add items to order in status " + activeOrder.getStatus()
            );
        }

        paymentRepository.findByOrderOrderId(activeOrder.getOrderId())
                .filter(payment -> payment.getStatus() == PaymentStatus.PENDING
                        || payment.getStatus() == PaymentStatus.PAID)
                .ifPresent(payment -> {
                    throw new RuntimeException(
                            "Cannot add items after payment has started."
                    );
                });

        return activeOrder;
    }

    private void appendItems(
            Order order,
            List<OrderItemRequest> itemRequests,
            String submissionNote
    ) {
        for (OrderItemRequest itemRequest : itemRequests) {
            Food food = foodRepository.findById(itemRequest.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Food not found"));

            if (food.getIsAvailable() == null || !food.getIsAvailable()) {
                throw new RuntimeException("Food unavailable");
            }

            BigDecimal subtotal = food.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            LocalDateTime now = LocalDateTime.now();

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .foodId(food.getFoodId())
                    .foodName(food.getFoodName())
                    .unitPrice(food.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .imageUrl(food.getImageUrl())
                    .emoji(food.getEmoji())
                    .note(resolveItemNote(itemRequest.getNote(), submissionNote))
                    .status(OrderItemStatus.CONFIRMED)
                    .createdAt(now)
                    .statusUpdatedAt(now)
                    .build();

            order.getItems().add(item);
        }
    }

    private String resolveItemNote(String itemNote, String submissionNote) {
        if (itemNote != null && !itemNote.isBlank()) {
            return itemNote.trim();
        }

        if (submissionNote != null && !submissionNote.isBlank()) {
            return submissionNote.trim();
        }

        return null;
    }

    private void recalculateTotalAmount(Order order) {
        BigDecimal totalAmount = order.getItems().stream()
                .filter(item -> item.getStatus() != OrderItemStatus.CANCELLED)
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long userId) {
        return orderRepository
                .findByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(String status) {
        OrderStatus orderStatus;

        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (Exception exception) {
            throw new RuntimeException("Invalid order status");
        }

        return orderRepository
                .findByStatusOrderByCreatedAtDesc(orderStatus)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenItemResponse> getKitchenItems(List<OrderItemStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one order item status is required"
            );
        }

        return orderItemRepository
                .findByStatusInOrderByCreatedAtAsc(statuses.stream().distinct().toList())
                .stream()
                .map(this::mapToKitchenItemResponse)
                .toList();
    }

    @Override
    @Transactional
    public KitchenItemResponse updateOrderItemStatus(
            Long orderItemId,
            UpdateOrderItemStatusRequest request
    ) {
        Long orderId = orderItemRepository.findOrderIdByOrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        Order order = findOrderForStatusUpdate(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = order.getItems().stream()
                .filter(candidate -> candidate.getOrderItemId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        validateItemStatusTransition(item.getStatus(), request.getStatus());

        item.setStatus(request.getStatus());
        item.setStatusUpdatedAt(LocalDateTime.now());

        if (request.getStatus() == OrderItemStatus.CANCELLED) {
            recalculateTotalAmount(order);
        }

        recalculateOrderStatus(order);
        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getStatus() == OrderStatus.CANCELLED) {
            releaseTable(savedOrder);
        }

        return mapToKitchenItemResponse(item);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request
    ) {
        Order order = findOrderForStatusUpdate(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (request.getStatus() == OrderStatus.CANCELLED) {
            cancelWholeOrder(order);
        } else if (request.getStatus() == OrderStatus.COMPLETED) {
            recalculateOrderStatus(order);

            if (order.getStatus() != OrderStatus.COMPLETED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Order can only be completed after payment"
                );
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order status is derived from order item statuses"
            );
        }

        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getStatus() == OrderStatus.COMPLETED
                || savedOrder.getStatus() == OrderStatus.CANCELLED) {
            releaseTable(savedOrder);
        }

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findOrderForStatusUpdate(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        cancelWholeOrder(order);
        Order savedOrder = orderRepository.save(order);
        releaseTable(savedOrder);
    }

    private void cancelWholeOrder(Order order) {
        validateOrderCanBeCancelled(order);
        LocalDateTime now = LocalDateTime.now();

        for (OrderItem item : order.getItems()) {
            if (item.getStatus() != OrderItemStatus.CANCELLED) {
                validateItemStatusTransition(item.getStatus(), OrderItemStatus.CANCELLED);
                item.setStatus(OrderItemStatus.CANCELLED);
                item.setStatusUpdatedAt(now);
            }
        }

        recalculateTotalAmount(order);
        recalculateOrderStatus(order);
    }

    private void validateItemStatusTransition(
            OrderItemStatus current,
            OrderItemStatus next
    ) {
        if (current == null) {
            throw new RuntimeException(
                    "Order item has no status; run the order item status backfill first"
            );
        }

        boolean valid = switch (current) {
            case CONFIRMED -> next == OrderItemStatus.PREPARING
                    || next == OrderItemStatus.CANCELLED;
            case PREPARING -> next == OrderItemStatus.READY;
            default -> false;
        };

        if (!valid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order item status transition: " + current + " -> " + next
            );
        }
    }

    private void recalculateOrderStatus(Order order) {
        if (isOrderPaid(order)) {
            order.setStatus(OrderStatus.COMPLETED);
            return;
        }

        List<OrderItem> items = order.getItems();

        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Order must contain at least one item");
        }

        if (items.stream().anyMatch(item -> item.getStatus() == null)) {
            throw new IllegalStateException(
                    "Order contains items without status; run the order item status backfill first"
            );
        }

        boolean allCancelled = items.stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.CANCELLED);

        if (allCancelled) {
            order.setStatus(OrderStatus.CANCELLED);
            return;
        }

        List<OrderItem> activeItems = items.stream()
                .filter(item -> item.getStatus() != OrderItemStatus.CANCELLED)
                .toList();

        boolean allReady = activeItems.stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.READY);

        if (allReady) {
            order.setStatus(OrderStatus.READY);
            return;
        }

        boolean allConfirmed = activeItems.stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.CONFIRMED);

        if (allConfirmed) {
            order.setStatus(OrderStatus.CONFIRMED);
            return;
        }

        order.setStatus(OrderStatus.PREPARING);
    }

    private boolean isOrderPaid(Order order) {
        if (order.getOrderId() == null) {
            return false;
        }

        return paymentRepository.findByOrderOrderId(order.getOrderId())
                .map(payment -> payment.getStatus() == PaymentStatus.PAID)
                .orElse(false);
    }

    private void validateOrderCanBeCancelled(Order order) {
        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Only PENDING or CONFIRMED orders can be cancelled");
        }

        if (isOrderPaid(order)) {
            throw new RuntimeException("Cannot cancel an order that has already been paid");
        }
    }

    private Optional<Order> findOrderForStatusUpdate(Long orderId) {
        Order orderSnapshot = orderRepository.findById(orderId).orElse(null);

        if (orderSnapshot == null) {
            return Optional.empty();
        }

        if (orderSnapshot.getTableId() != null) {
            restaurantTableRepository.findByTableIdForUpdate(orderSnapshot.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));
        }

        return orderRepository.findByOrderId(orderId);
    }

    private void releaseTable(Order order) {
        if (order.getTableId() == null) {
            return;
        }

        RestaurantTable table = restaurantTableRepository
                .findById(order.getTableId())
                .orElse(null);

        if (table == null) {
            return;
        }

        if (table.getCurrentOrderCode() == null
                || !table.getCurrentOrderCode().equals(order.getOrderCode())) {
            return;
        }

        table.setStatus(TableStatus.EMPTY);
        table.setCurrentOrderCode(null);
        table.setReservedBy(null);
        restaurantTableRepository.save(table);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getOrderItemId(),
                        item.getFoodId(),
                        item.getFoodName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal(),
                        item.getImageUrl(),
                        item.getEmoji(),
                        item.getNote() != null ? item.getNote() : order.getNote(),
                        item.getStatus(),
                        item.getCreatedAt() != null ? item.getCreatedAt() : order.getCreatedAt(),
                        item.getStatusUpdatedAt()
                ))
                .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderCode(),
                order.getUser() != null ? order.getUser().getUserId() : null,
                order.getUser() != null
                        ? order.getUser().getUsername()
                        : order.getCustomerName(),
                order.getUser() != null ? order.getUser().getEmail() : null,
                order.getTableId(),
                order.getTableName(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getNote(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }

    private KitchenItemResponse mapToKitchenItemResponse(OrderItem item) {
        Order order = item.getOrder();

        return new KitchenItemResponse(
                item.getOrderItemId(),
                order.getOrderId(),
                order.getOrderCode(),
                order.getTableId(),
                order.getTableName(),
                item.getFoodId(),
                item.getFoodName(),
                item.getQuantity(),
                item.getImageUrl(),
                item.getEmoji(),
                item.getNote() != null ? item.getNote() : order.getNote(),
                item.getStatus(),
                item.getCreatedAt() != null ? item.getCreatedAt() : order.getCreatedAt(),
                item.getStatusUpdatedAt()
        );
    }

    private String generateOrderCode() {
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 9000) + 1000;

        return "ORD-" + time + "-" + random;
    }
}
