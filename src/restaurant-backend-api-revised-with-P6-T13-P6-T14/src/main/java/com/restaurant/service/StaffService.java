package com.restaurant.service;

import com.restaurant.dto.order.OrderResponse;
import com.restaurant.dto.staff.*;
import com.restaurant.entity.*;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.*;
import com.restaurant.repository.*;
import com.restaurant.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CouponService couponService;
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public List<TableResponse> getTables(String status) {
        if (status == null || status.isBlank()) {
            return tableRepository.findAll().stream().map(this::toTableResponse).toList();
        }
        TableStatus tableStatus = parseTableStatus(status);
        return tableRepository.findByTableStatus(tableStatus).stream().map(this::toTableResponse).toList();
    }

    public TableResponse getTable(Long tableId) {
        return toTableResponse(getTableEntity(tableId));
    }

    @Transactional
    public TableResponse updateTableStatus(Long tableId, UpdateTableStatusRequest request) {
        RestaurantTable table = getTableEntity(tableId);
        table.setTableStatus(parseTableStatus(request.getTableStatus()));
        return toTableResponse(tableRepository.save(table));
    }

    public List<OrderResponse> getStaffOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(orderService::toResponse).toList();
    }

    public OrderResponse getStaffOrderDetail(Long orderId) {
        return orderService.toResponse(getOrder(orderId));
    }

    @Transactional
    public OrderResponse createCounterOrder(StaffCreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Counter order must contain at least one item");
        }

        User staff = currentUserService.getCurrentUser();
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        RestaurantTable table = null;
        if (request.getTableId() != null) {
            table = getTableEntity(request.getTableId());
            if (table.getTableStatus() == TableStatus.OUT_OF_SERVICE) {
                throw new BadRequestException("Selected table is out of service");
            }
        }

        Order order = Order.builder()
                .customer(customer)
                .staff(staff)
                .orderType(parseOrderType(request.getOrderType()))
                .orderStatus(OrderStatus.CONFIRMED)
                .subtotalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .note(buildStaffOrderNote(table, request.getNote()))
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (StaffOrderItemRequest itemRequest : request.getItems()) {
            Food food = foodRepository.findById(itemRequest.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found: " + itemRequest.getFoodId()));
            if (food.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
                throw new BadRequestException("Food is not available: " + food.getFoodName());
            }
            BigDecimal lineTotal = food.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .food(food)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(food.getPrice())
                    .itemStatus(OrderItemStatus.PENDING)
                    .kitchenNote(itemRequest.getKitchenNote())
                    .build());
        }

        Coupon coupon = couponService.findValidByCode(request.getCouponCode(), subtotal);
        BigDecimal discount = coupon == null ? BigDecimal.ZERO : couponService.calculateDiscount(coupon, subtotal);
        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO);

        order.setCoupon(coupon);
        order.setSubtotalAmount(subtotal);
        order.setDiscountAmount(discount);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        if (table != null) {
            table.setTableStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        }

        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isBlank()) {
            Payment payment = Payment.builder()
                    .order(savedOrder)
                    .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                    .paymentStatus(PaymentStatus.PENDING)
                    .amount(savedOrder.getTotalAmount())
                    .build();
            paymentRepository.save(payment);
        }

        return orderService.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, StaffOrderStatusRequest request) {
        Order order = getOrder(orderId);
        OrderStatus newStatus = parseOrderStatus(request.getOrderStatus());
        validateStaffStatusTransition(order.getOrderStatus(), newStatus);
        order.setOrderStatus(newStatus);
        return orderService.toResponse(orderRepository.save(order));
    }

    @Transactional
    public StaffPaymentResponse createPaymentForOrder(Long orderId, StaffPaymentConfirmRequest request) {
        Order order = getOrder(orderId);
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                .paymentStatus(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .transactionCode(request.getTransactionCode())
                .build();
        return toPaymentResponse(paymentRepository.save(payment));
    }

    @Transactional
    public StaffPaymentResponse confirmPaymentByOrder(Long orderId, StaffPaymentConfirmRequest request) {
        Order order = getOrder(orderId);
        Payment payment = paymentRepository.findTopByOrder_OrderIdOrderByCreatedAtDesc(orderId)
                .orElseGet(() -> Payment.builder()
                        .order(order)
                        .amount(order.getTotalAmount())
                        .paymentStatus(PaymentStatus.PENDING)
                        .build());

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Payment was already confirmed");
        }

        payment.setPaymentMethod(parsePaymentMethod(request.getPaymentMethod()));
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setTransactionCode(request.getTransactionCode());
        payment.setPaidAt(LocalDateTime.now());

        if (order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.PENDING_PAYMENT) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }

        // Important business rule: this method confirms payment only.
        // Invoice must be generated by Invoice API after PaymentStatus = PAID.
        return toPaymentResponse(paymentRepository.save(payment));
    }

    private RestaurantTable getTableEntity(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private String buildStaffOrderNote(RestaurantTable table, String note) {
        if (table == null) {
            return note;
        }
        String tableNote = "Table " + table.getTableNumber();
        if (note == null || note.isBlank()) {
            return tableNote;
        }
        return tableNote + " - " + note;
    }

    private void validateStaffStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.CANCELLED || current == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot update a " + current + " order");
        }
        if (next == OrderStatus.PENDING_PAYMENT && current == OrderStatus.PREPARING) {
            throw new BadRequestException("Cannot move a preparing order back to pending payment");
        }
    }

    private TableStatus parseTableStatus(String value) {
        try {
            return TableStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid tableStatus. Allowed: AVAILABLE, RESERVED, OCCUPIED, OUT_OF_SERVICE");
        }
    }

    private OrderType parseOrderType(String value) {
        if (value == null || value.isBlank()) {
            return OrderType.DINE_IN;
        }
        try {
            return OrderType.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid orderType. Allowed: DINE_IN, TAKEAWAY, DELIVERY");
        }
    }

    private OrderStatus parseOrderStatus(String value) {
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid orderStatus. Allowed: PENDING, PENDING_PAYMENT, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED");
        }
    }

    private PaymentMethod parsePaymentMethod(String value) {
        try {
            return PaymentMethod.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid paymentMethod. Allowed: CASH, ONLINE, QR, CARD, BANK_TRANSFER");
        }
    }

    private TableResponse toTableResponse(RestaurantTable table) {
        return TableResponse.builder()
                .tableId(table.getTableId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .tableStatus(String.valueOf(table.getTableStatus()))
                .location(table.getLocation())
                .build();
    }

    private StaffPaymentResponse toPaymentResponse(Payment payment) {
        return StaffPaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getOrderId())
                .paymentMethod(String.valueOf(payment.getPaymentMethod()))
                .paymentStatus(String.valueOf(payment.getPaymentStatus()))
                .amount(payment.getAmount())
                .transactionCode(payment.getTransactionCode())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
