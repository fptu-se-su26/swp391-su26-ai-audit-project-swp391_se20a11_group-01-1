package com.restaurant.controller;

import com.restaurant.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffDemoController {

    @GetMapping("/demo")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> staffDemo() {
        return ResponseEntity.ok(ApiResponse.ok("Staff access granted", "Staff can manage tables, counter orders and payment confirmation."));
    }
}
