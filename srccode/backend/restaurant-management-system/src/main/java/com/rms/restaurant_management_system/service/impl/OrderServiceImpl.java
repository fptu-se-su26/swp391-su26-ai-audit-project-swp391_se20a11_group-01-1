package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.CreateOrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.CouponValidationResponse;
import com.rms.restaurant_management_system.dto.response.OrderDetailResponse;
import com.rms.restaurant_management_system.dto.response.OrderResponse;
import com.rms.restaurant_management_system.entity.*;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.*;
import com.rms.restaurant_management_system.service.interfaces.CartService;
import com.rms.restaurant_management_system.service.interfaces.CouponService;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final ReservationRepository reservationRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(String userEmail, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot create an order from an empty cart");
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getFood().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Coupon coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalTotal = subtotal;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            CouponValidationResponse validation = couponService.validateAndCalculateDiscount(request.getCouponCode(), subtotal);
            if (!validation.isValid()) {
                throw new BadRequestException(validation.getMessage());
            }
            discountAmount = validation.getDiscountAmount();
            finalTotal = validation.getFinalTotal();
            
            coupon = couponRepository.findByCode(request.getCouponCode().toUpperCase().trim())
                    .orElseThrow(() -> new BadRequestException("Coupon not found"));
            
            // Increment usage count
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        Reservation reservation = null;
        if (request.getReservationId() != null) {
            reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
            
            if (!reservation.getUser().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("Reservation does not belong to you");
            }
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .reservation(reservation)
                .coupon(coupon)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .finalTotal(finalTotal)
                .status(OrderStatus.PENDING)
                .build();

        List<OrderDetail> orderDetails = cart.getItems().stream()
                .map(cartItem -> OrderDetail.builder()
                        .order(order)
                        .food(cartItem.getFood())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getFood().getPrice())
                        .lineTotal(cartItem.getFood().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        order.setDetails(orderDetails);
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(userEmail);

        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id, String userEmail, boolean isAdminOrStaff) {
        Order order = getOrderEntity(id);
        if (!isAdminOrStaff && !order.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = getOrderEntity(id);
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot update a cancelled order");
        }
        
        order.setStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id, String userEmail, boolean isAdminOrStaff) {
        Order order = getOrderEntity(id);

        if (!isAdminOrStaff && !order.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    private Order getOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderDetailResponse> detailResponses = order.getDetails().stream()
                .map(detail -> OrderDetailResponse.builder()
                        .id(detail.getId())
                        .foodId(detail.getFood().getId())
                        .foodName(detail.getFood().getName())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .lineTotal(detail.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .reservationId(order.getReservation() != null ? order.getReservation().getId() : null)
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .finalTotal(order.getFinalTotal())
                .status(order.getStatus())
                .details(detailResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
