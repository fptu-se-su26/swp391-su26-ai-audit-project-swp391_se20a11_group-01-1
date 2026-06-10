package com.restaurant.controller;

import com.restaurant.common.ApiResponse;
import com.restaurant.dto.staff.*;
import com.restaurant.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class StaffController {
    private final StaffService staffService;

    @GetMapping("/tables")
    public ApiResponse<?> getTables(@RequestParam(required = false) String status) {
        return ApiResponse.ok(staffService.getTables(status));
    }

    @GetMapping("/tables/{tableId}")
    public ApiResponse<?> getTable(@PathVariable Long tableId) {
        return ApiResponse.ok(staffService.getTable(tableId));
    }

    @PatchMapping("/tables/{tableId}/status")
    public ApiResponse<?> updateTableStatus(@PathVariable Long tableId,
                                            @Valid @RequestBody UpdateTableStatusRequest request) {
        return ApiResponse.ok(staffService.updateTableStatus(tableId, request));
    }

    @GetMapping("/orders")
    public ApiResponse<?> getOrders() {
        return ApiResponse.ok(staffService.getStaffOrders());
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<?> getOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.ok(staffService.getStaffOrderDetail(orderId));
    }

    @PostMapping("/orders")
    public ApiResponse<?> createCounterOrder(@Valid @RequestBody StaffCreateOrderRequest request) {
        return ApiResponse.ok(staffService.createCounterOrder(request));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ApiResponse<?> updateOrderStatus(@PathVariable Long orderId,
                                            @Valid @RequestBody StaffOrderStatusRequest request) {
        return ApiResponse.ok(staffService.updateOrderStatus(orderId, request));
    }

    @PostMapping("/orders/{orderId}/payments")
    public ApiResponse<?> createPayment(@PathVariable Long orderId,
                                        @Valid @RequestBody StaffPaymentConfirmRequest request) {
        return ApiResponse.ok(staffService.createPaymentForOrder(orderId, request));
    }

    @PostMapping("/orders/{orderId}/payments/confirm")
    public ApiResponse<?> confirmPayment(@PathVariable Long orderId,
                                         @Valid @RequestBody StaffPaymentConfirmRequest request) {
        return ApiResponse.ok(staffService.confirmPaymentByOrder(orderId, request));
    }
}
