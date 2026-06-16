package com.restaurant.management.service;

import com.restaurant.management.dto.admin.UpdateUserStatusRequest;
import com.restaurant.management.dto.admin.UserDetailResponse;
import com.restaurant.management.dto.admin.UserListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    Page<UserListResponse> getUsers(String keyword, String status, String role, Pageable pageable);
    UserDetailResponse getUserDetail(Long id);
    void updateUserStatus(Long id, UpdateUserStatusRequest request);
}
