package com.restaurant.service;

import com.restaurant.constant.OrderStatus;
import com.restaurant.dto.order.*;
import com.restaurant.entity.*;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ForbiddenException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public OrderDetailResponse createOrderFromCart(CreateOrderRequest request) {
        User customer = getCurrentUser();

        Cart cart = cartRepository.findByUserId(customer.getId())
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        RestaurantTable table = null;
        if (request.getTableId() != null) {
            table = restaurantTableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
        }

        BigDecimal totalAmount = calculateTotalAmount(cartItems);
        Coupon coupon = findCouponIfPresent(request.getCouponCode());
        BigDecimal discountAmount = calculateDiscount(totalAmount, coupon);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .orderType("ONLINE")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .restaurantTable(table)
                .coupon(coupon)
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> buildOrderItem(savedOrder, cartItem, request.getNote()))
                .toList();

        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteByCartId(cart.getId());

        return getOrderDetail(savedOrder.getId());
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getMyOrders() {
        User customer = getCurrentUser();
        return orderRepository.findByCustomer_IdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getMyOrderDetail(Long orderId) {
        User customer = getCurrentUser();
        Order order = orderRepository.findOrderDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())) {
            throw new ForbiddenException("You cannot view this order");
        }

        return toDetailResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findOrderDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toDetailResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetailByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toDetailResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public OrderDetailResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        String status = normalizeStatus(request.getOrderStatus());
        order.setOrderStatus(status);
        orderRepository.save(order);

        return getOrderDetail(orderId);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private BigDecimal calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getFoodItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Coupon findCouponIfPresent(String couponCode) {
        if (couponCode == null || couponCode.isBlank()) {
            return null;
        }
        return couponRepository.findByCode(couponCode.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new BadRequestException("Coupon not found"));
    }

    private BigDecimal calculateDiscount(BigDecimal totalAmount, Coupon coupon) {
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        if (coupon.getMinOrderAmount() != null && totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Order amount does not meet coupon condition");
        }

        if ("PERCENT".equalsIgnoreCase(coupon.getDiscountType())) {
            return totalAmount.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
        }

        if ("AMOUNT".equalsIgnoreCase(coupon.getDiscountType())) {
            return coupon.getDiscountValue().min(totalAmount);
        }

        throw new BadRequestException("Invalid coupon discount type");
    }

    private OrderItem buildOrderItem(Order order, CartItem cartItem, String note) {
        BigDecimal unitPrice = cartItem.getFoodItem().getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return OrderItem.builder()
                .order(order)
                .foodItem(cartItem.getFoodItem())
                .quantity(cartItem.getQuantity())
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .itemStatus("WAITING")
                .note(note)
                .build();
    }

    private String generateOrderCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }

    private String normalizeStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        List<String> allowed = List.of(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.READY,
                OrderStatus.COMPLETED,
                OrderStatus.CANCELLED
        );

        if (!allowed.contains(normalized)) {
            throw new BadRequestException("Invalid order status");
        }
        return normalized;
    }

    private OrderSummaryResponse toSummaryResponse(Order order) {
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderDetailResponse toDetailResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());

        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .createdAt(order.getCreatedAt())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getFullName() : null)
                .tableId(order.getRestaurantTable() != null ? order.getRestaurantTable().getId() : null)
                .tableNumber(order.getRestaurantTable() != null ? order.getRestaurantTable().getTableNumber() : null)
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .items(items.stream().map(this::toItemResponse).toList())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        FoodItem food = item.getFoodItem();
        return OrderItemResponse.builder()
                .orderItemId(item.getId())
                .foodItemId(food.getId())
                .foodName(food.getFoodName())
                .imageUrl(food.getImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .itemStatus(item.getItemStatus())
                .note(item.getNote())
                .build();
    }
}
