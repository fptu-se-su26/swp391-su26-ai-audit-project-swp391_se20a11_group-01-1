package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.OrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.OrderResponse;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.service.interfaces.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request,
                                     @AuthenticationPrincipal User actor) {
        if (isCustomer(actor)) {
            request.setUserId(actor.getUserId());
        }
        return orderService.createOrder(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("@domainAuthorization.canAccessOrder(#orderId, authentication)")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/customer/{userId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<OrderResponse> getOrdersByCustomer(@PathVariable Long userId) {
        return orderService.getOrdersByCustomer(userId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<OrderResponse> getMyOrders(@AuthenticationPrincipal User actor) {
        return orderService.getOrdersByCustomer(actor.getUserId());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<OrderResponse> getOrdersByStatus(@PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return orderService.updateOrderStatus(orderId, request);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("@domainAuthorization.canAccessOrder(#orderId, authentication)")
    public String cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return "Order cancelled successfully";
    }

    private boolean isCustomer(User user) {
        return user != null && "CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName());
    }
}
