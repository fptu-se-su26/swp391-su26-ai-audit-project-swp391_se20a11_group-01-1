package com.restaurant.controller;

import com.restaurant.common.ApiResponse;
import com.restaurant.dto.kitchen.UpdateKitchenItemStatusRequest;
import com.restaurant.service.KitchenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kitchen")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('KITCHEN','ADMIN')")
public class KitchenController {
    private final KitchenService kitchenService;

    @GetMapping("/orders")
    public ApiResponse<?> getCookingOrders(@RequestParam(required = false) String status) {
        return ApiResponse.ok(kitchenService.getCookingOrders(status));
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<?> getCookingOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.ok(kitchenService.getCookingOrderDetail(orderId));
    }

    @GetMapping("/order-items")
    public ApiResponse<?> getItemsByStatus(@RequestParam(required = false) String status) {
        return ApiResponse.ok(kitchenService.getItemsByStatus(status));
    }

    @PatchMapping("/order-items/{orderItemId}/status")
    public ApiResponse<?> updateItemStatus(@PathVariable Long orderItemId,
                                           @Valid @RequestBody UpdateKitchenItemStatusRequest request) {
        return ApiResponse.ok(kitchenService.updateOrderItemStatus(orderItemId, request));
    }

    @PostMapping("/order-items/{orderItemId}/accept")
    public ApiResponse<?> acceptItem(@PathVariable Long orderItemId) {
        return ApiResponse.ok(kitchenService.acceptOrderItem(orderItemId));
    }

    @PostMapping("/order-items/{orderItemId}/reject")
    public ApiResponse<?> rejectItem(@PathVariable Long orderItemId,
                                     @Valid @RequestBody UpdateKitchenItemStatusRequest request) {
        return ApiResponse.ok(kitchenService.rejectOrderItem(orderItemId, request));
    }
}
