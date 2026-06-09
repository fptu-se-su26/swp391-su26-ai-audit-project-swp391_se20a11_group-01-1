package com.restaurant.controller;

import com.restaurant.dto.order.*;
import com.restaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDetailResponse data = orderService.createOrderFromCart(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order created successfully", data));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getMyOrders() {
        return ResponseEntity.ok(ApiResponse.ok("Get order history successfully", orderService.getMyOrders()));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getMyOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok("Get order detail successfully", orderService.getMyOrderDetail(orderId)));
    }

    @GetMapping("/orders/code/{orderCode}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'STAFF', 'KITCHEN', 'ADMIN', 'ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_KITCHEN', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderByCode(@PathVariable String orderCode) {
        return ResponseEntity.ok(ApiResponse.ok("Get order detail successfully", orderService.getOrderDetailByCode(orderCode)));
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasAnyAuthority('STAFF', 'ADMIN', 'ROLE_STAFF', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.ok("Get all orders successfully", orderService.getAllOrders()));
    }

    @GetMapping("/admin/orders/{orderId}")
    @PreAuthorize("hasAnyAuthority('STAFF', 'ADMIN', 'ROLE_STAFF', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok("Get order detail successfully", orderService.getOrderDetail(orderId)));
    }

    @PatchMapping("/admin/orders/{orderId}/status")
    @PreAuthorize("hasAnyAuthority('STAFF', 'ADMIN', 'ROLE_STAFF', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Order status updated successfully", orderService.updateOrderStatus(orderId, request)));
    }
}
