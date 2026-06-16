package com.restaurant.management.controller;

import com.restaurant.management.dto.order.CancelOrderRequest;
import com.restaurant.management.dto.order.CheckoutRequest;
import com.restaurant.management.dto.order.OrderDetailResponse;
import com.restaurant.management.dto.order.OrderResponse;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request) {
        OrderDetailResponse response = orderService.checkout(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Checkout successful"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {
        List<OrderResponse> response = orderService.getMyOrders();
        return ResponseEntity.ok(ApiResponse.success(response, "Orders fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getMyOrder(@PathVariable Long id) {
        OrderDetailResponse response = orderService.getMyOrder(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Order fetched successfully"));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @Valid @RequestBody CancelOrderRequest request) {
        OrderResponse response = orderService.cancelOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Order cancelled successfully"));
    }
}
