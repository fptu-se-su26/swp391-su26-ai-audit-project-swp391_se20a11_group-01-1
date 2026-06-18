package com.restaurant.management.controller;

import com.restaurant.management.dto.kitchen.KitchenOrderResponse;
import com.restaurant.management.dto.kitchen.KitchenUpdateOrderStatusRequest;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.KitchenOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_KITCHEN', 'ROLE_ADMIN')")
public class KitchenOrderController {

    private final KitchenOrderService kitchenOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<KitchenOrderResponse>>> getActiveOrders() {
        return ResponseEntity.ok(ApiResponse.success(kitchenOrderService.getActiveOrders(), "Active orders fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KitchenOrderResponse>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(kitchenOrderService.getOrderById(id), "Order fetched successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<KitchenOrderResponse>> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody KitchenUpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(kitchenOrderService.updateOrderStatus(id, request), "Order status updated successfully"));
    }
}
