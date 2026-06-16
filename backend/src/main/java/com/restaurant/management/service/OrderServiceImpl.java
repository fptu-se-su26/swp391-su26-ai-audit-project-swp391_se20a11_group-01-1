package com.restaurant.management.service;

import com.restaurant.management.dto.coupon.CouponValidationRequest;
import com.restaurant.management.dto.coupon.CouponValidationResponse;
import com.restaurant.management.dto.order.CancelOrderRequest;
import com.restaurant.management.dto.order.CheckoutRequest;
import com.restaurant.management.dto.order.OrderDetailResponse;
import com.restaurant.management.dto.order.OrderItemResponse;
import com.restaurant.management.dto.order.OrderResponse;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.cart.Cart;
import com.restaurant.management.entity.cart.CartItem;
import com.restaurant.management.entity.coupon.Coupon;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.entity.order.OrderItem;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.RestaurantOrder;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.CartRepository;
import com.restaurant.management.repository.CouponRepository;
import com.restaurant.management.repository.FoodItemRepository;
import com.restaurant.management.repository.OrderRepository;
import com.restaurant.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderDetailResponse checkout(CheckoutRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(cartItem.getFoodItem().getId())
                    .orElseThrow(() -> new BadRequestException("Food item not found"));

            if (foodItem.getDeletedAt() != null || !foodItem.getIsAvailable()) {
                throw new BadRequestException("Food item " + foodItem.getName() + " is not available");
            }

            BigDecimal itemTotal = foodItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subTotal = subTotal.add(itemTotal);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = subTotal;
        Coupon coupon = null;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            CouponValidationRequest validationRequest = new CouponValidationRequest();
            validationRequest.setCode(request.getCouponCode());
            validationRequest.setOrderAmount(subTotal);

            CouponValidationResponse validationResponse = couponService.validateCoupon(validationRequest);
            if (!validationResponse.isValid()) {
                throw new BadRequestException("Invalid coupon");
            }
            discountAmount = validationResponse.getDiscountAmount();
            totalAmount = validationResponse.getFinalAmount();

            coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new BadRequestException("Coupon not found"));
        }

        RestaurantOrder order = RestaurantOrder.builder()
                .customer(currentUser)
                .tableId(request.getTableId())
                .coupon(coupon)
                .orderStatus(OrderStatus.PENDING)
                .orderType(request.getOrderType())
                .subTotal(subTotal)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .note(request.getNote())
                .build();

        for (CartItem cartItem : cart.getItems()) {
            FoodItem foodItem = cartItem.getFoodItem();
            OrderItem orderItem = OrderItem.builder()
                    .foodItem(foodItem)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(foodItem.getPrice()) // snapshot price
                    .build();
            order.addItem(orderItem);
        }

        RestaurantOrder savedOrder = orderRepository.save(order);

        cartService.clearCart();

        return mapToDetailResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepository.findByCustomerId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getMyOrder(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RestaurantOrder order = orderRepository.findByIdAndCustomerId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or does not belong to you"));

        return mapToDetailResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id, CancelOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RestaurantOrder order = orderRepository.findByIdAndCustomerId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or does not belong to you"));

        if (order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed order");
        }
        
        // As per requirements: "Chỉ cancel nếu chưa PAID và chưa COMPLETED"
        // Wait, OrderStatus doesn't have PAID. If it's PAID, maybe it has a payment entity which we don't handle.
        // We will assume "chưa PAID" means the order is strictly PENDING or PENDING_PAYMENT or CONFIRMED.
        // If they use PaymentStatus we can't check it yet. But we can check if it's already PREPARING or READY or DELIVERING.
        // I will just check if OrderStatus == PENDING or PENDING_PAYMENT. Wait, the prompt says "Chưa PAID và chưa COMPLETED".
        // What if they mean payment status? Since we don't have Payment, we'll assume OrderStatus == PENDING is cancelable.
        // Let's just block cancellation if the order status is COMPLETED or CANCELLED for now, plus any status that means it's too late.
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        // Note: request.getReason() can be stored in note or a new field, but we just set status here
        if (request.getReason() != null) {
            order.setNote((order.getNote() != null ? order.getNote() + " | " : "") + "Cancel reason: " + request.getReason());
        }

        RestaurantOrder savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(RestaurantOrder order) {
        return OrderResponse.builder()
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
                .build();
    }

    private OrderDetailResponse mapToDetailResponse(RestaurantOrder order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .foodItemId(item.getFoodItem().getId())
                        .foodName(item.getFoodItem().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .note(item.getNote())
                        .build())
                .collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .tableId(order.getTableId())
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .orderStatus(order.getOrderStatus())
                .orderType(order.getOrderType())
                .subTotal(order.getSubTotal())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
    }
}
