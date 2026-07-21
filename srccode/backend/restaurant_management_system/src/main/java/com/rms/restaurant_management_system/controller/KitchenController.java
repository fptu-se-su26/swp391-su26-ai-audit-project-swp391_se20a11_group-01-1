package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.UpdateOrderItemStatusRequest;
import com.rms.restaurant_management_system.dto.response.KitchenItemResponse;
import com.rms.restaurant_management_system.service.interfaces.KitchenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;

    @GetMapping("/active-items")
    public List<KitchenItemResponse> getActiveKitchenItems() {
        return kitchenService.getActiveKitchenItems();
    }

    @PatchMapping("/items/{itemId}/status")
    public String updateItemStatus(@PathVariable Long itemId, @Valid @RequestBody UpdateOrderItemStatusRequest request) {
        kitchenService.updateItemStatus(itemId, request);
        return "Item status updated successfully";
    }
}
