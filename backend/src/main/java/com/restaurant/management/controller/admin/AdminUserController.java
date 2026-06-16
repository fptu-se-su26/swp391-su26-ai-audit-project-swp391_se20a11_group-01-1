package com.restaurant.management.controller.admin;

import com.restaurant.management.dto.admin.UpdateUserStatusRequest;
import com.restaurant.management.dto.admin.UserDetailResponse;
import com.restaurant.management.dto.admin.UserListResponse;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserListResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserListResponse> users = userManagementService.getUsers(keyword, status, role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@PathVariable Long id) {
        UserDetailResponse userDetail = userManagementService.getUserDetail(id);
        return ResponseEntity.ok(ApiResponse.success(userDetail, "User details fetched successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        userManagementService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "User status updated successfully"));
    }
}
