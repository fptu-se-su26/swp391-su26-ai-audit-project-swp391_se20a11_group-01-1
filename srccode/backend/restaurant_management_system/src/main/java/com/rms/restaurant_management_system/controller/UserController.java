package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.response.StaffCustomerResponse;
import com.rms.restaurant_management_system.dto.response.UserResponse;
import com.rms.restaurant_management_system.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.rms.restaurant_management_system.dto.request.UpdateUserRoleRequest;
import com.rms.restaurant_management_system.dto.request.UpdateUserStatusRequest;
import java.util.List;
import com.rms.restaurant_management_system.dto.request.UpdateMyProfileRequest;
import com.rms.restaurant_management_system.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<StaffCustomerResponse> getStaffCustomers() {
        return userService.getStaffCustomers();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getProfileByEmail(@RequestParam String email) {
        return userService.getProfileByEmail(email);
    }

    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication authentication) {
        return userService.getProfileByEmail(currentUser(authentication).getEmail());
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@Valid @RequestBody UpdateMyProfileRequest request,
                                        Authentication authentication) {
        return userService.updateMyProfile(currentUser(authentication).getUserId(), request);
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserRole(
            @PathVariable Long userId,
            @RequestBody UpdateUserRoleRequest request,
            Authentication authentication
    ) {
        if (currentUser(authentication).getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không thể tự đổi vai trò của chính mình");
        }
        return userService.updateUserRole(userId, request);
    }

    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UpdateUserStatusRequest request,
            Authentication authentication
    ) {
        if (currentUser(authentication).getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không thể tự khóa tài khoản của chính mình");
        }
        return userService.updateUserStatus(userId, request);
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new RuntimeException("Chưa đăng nhập");
        }
        return user;
    }
}
