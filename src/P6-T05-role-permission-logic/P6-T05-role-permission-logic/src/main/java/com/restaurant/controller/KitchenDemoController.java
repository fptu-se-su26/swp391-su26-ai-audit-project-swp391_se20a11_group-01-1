package com.restaurant.controller;

import com.restaurant.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kitchen")
public class KitchenDemoController {

    @GetMapping("/demo")
    @PreAuthorize("hasAnyRole('KITCHEN', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> kitchenDemo() {
        return ResponseEntity.ok(ApiResponse.ok("Kitchen access granted", "Kitchen can view cooking orders and update order item status."));
    }
}
