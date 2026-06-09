package com.restaurant.controller;

import com.restaurant.constant.AppRole;
import com.restaurant.dto.admin.RoleAssignmentRequest;
import com.restaurant.dto.common.ApiResponse;
import com.restaurant.entity.User;
import com.restaurant.service.RoleManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDemoController {

    private final RoleManagementService roleManagementService;

    @GetMapping("/demo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminDemo() {
        return ResponseEntity.ok(ApiResponse.ok("Admin access granted", "Admin can manage users, menu, coupon and reports."));
    }

    @PutMapping("/users/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> assignRole(@Valid @RequestBody RoleAssignmentRequest request) {
        User user = roleManagementService.assignRole(request.getUserId(), request.getRole());
        String message = "Assigned role " + request.getRole().name() + " to user id " + user.getId();
        return ResponseEntity.ok(ApiResponse.ok(message, message));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppRole[]>> getRoles() {
        return ResponseEntity.ok(ApiResponse.ok("Available roles", AppRole.values()));
    }
}
