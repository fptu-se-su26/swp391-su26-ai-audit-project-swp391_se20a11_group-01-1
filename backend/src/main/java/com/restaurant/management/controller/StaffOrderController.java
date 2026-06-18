package com.restaurant.management.controller;

import com.restaurant.management.dto.staff.StaffCreateOrderRequest;
import com.restaurant.management.dto.staff.StaffOrderResponse;
import com.restaurant.management.dto.staff.StaffUpdateOrderStatusRequest;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.StaffOrderService;
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
@RequestMapping("/api/staff/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
public class StaffOrderController {

    private final StaffOrderService staffOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<StaffOrderResponse>> createOrder(@Valid @RequestBody StaffCreateOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffOrderService.createOrder(request), "Order created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffOrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success(staffOrderService.getAllOrders(), "Orders fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffOrderResponse>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(staffOrderService.getOrderById(id), "Order fetched successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StaffOrderResponse>> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody StaffUpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffOrderService.updateOrderStatus(id, request), "Order status updated successfully"));
    }
}
