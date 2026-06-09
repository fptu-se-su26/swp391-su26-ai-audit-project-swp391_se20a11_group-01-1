package com.restaurant.controller;

import com.restaurant.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerDemoController {

    @GetMapping("/demo")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> customerDemo() {
        return ResponseEntity.ok(ApiResponse.ok("Customer access granted", "Customer can view menu, cart, order history and invoice."));
    }
}
