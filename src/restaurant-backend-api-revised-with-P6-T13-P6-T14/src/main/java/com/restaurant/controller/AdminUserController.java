package com.restaurant.controller;

import com.restaurant.common.ApiResponse;
import com.restaurant.dto.admin.RoleAssignmentRequest;
import com.restaurant.dto.admin.UserAdminRequest;
import com.restaurant.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<?> getUsers() { return ApiResponse.ok(adminUserService.getUsers()); }

    @GetMapping("/{userId}")
    public ApiResponse<?> getUser(@PathVariable Long userId) { return ApiResponse.ok(adminUserService.getUser(userId)); }

    @PutMapping("/{userId}")
    public ApiResponse<?> updateUser(@PathVariable Long userId, @Valid @RequestBody UserAdminRequest request) {
        return ApiResponse.ok(adminUserService.updateUser(userId, request));
    }

    @PutMapping("/{userId}/roles")
    public ApiResponse<?> assignRoles(@PathVariable Long userId, @Valid @RequestBody RoleAssignmentRequest request) {
        return ApiResponse.ok(adminUserService.assignRoles(userId, request));
    }

    @PatchMapping("/{userId}/lock")
    public ApiResponse<?> lockUser(@PathVariable Long userId) { return ApiResponse.ok(adminUserService.lockUser(userId)); }

    @PatchMapping("/{userId}/activate")
    public ApiResponse<?> activateUser(@PathVariable Long userId) { return ApiResponse.ok(adminUserService.activateUser(userId)); }
}
