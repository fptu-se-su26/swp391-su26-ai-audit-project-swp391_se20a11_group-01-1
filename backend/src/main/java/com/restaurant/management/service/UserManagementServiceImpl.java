package com.restaurant.management.service;

import com.restaurant.management.dto.admin.RoleResponse;
import com.restaurant.management.dto.admin.UpdateUserStatusRequest;
import com.restaurant.management.dto.admin.UserDetailResponse;
import com.restaurant.management.dto.admin.UserListResponse;
import com.restaurant.management.dto.admin.UserStatus;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.auth.UserRole;
import com.restaurant.management.exception.BusinessException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.CustomerProfileRepository;
import com.restaurant.management.repository.UserRepository;
import com.restaurant.management.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final CustomerProfileRepository customerProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserListResponse> getUsers(String keyword, String status, String role, Pageable pageable) {
        Boolean isActive = null;
        if ("ACTIVE".equalsIgnoreCase(status)) {
            isActive = true;
        } else if ("LOCKED".equalsIgnoreCase(status) || "DISABLED".equalsIgnoreCase(status)) {
            isActive = false;
        }

        Page<User> usersPage = userRepository.searchUsers(keyword, isActive, role, pageable);

        return usersPage.map(user -> {
            List<UserRole> roles = userRoleRepository.findByUserId(user.getId());
            String fullName = customerProfileRepository.findByUserId(user.getId())
                    .map(profile -> profile.getFullName())
                    .orElse("");

            return UserListResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .isActive(user.getIsActive())
                    .roles(roles.stream().map(r -> r.getRole().getName()).collect(Collectors.toList()))
                    .fullName(fullName)
                    .build();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        List<RoleResponse> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(ur -> RoleResponse.builder()
                        .id(ur.getRole().getId())
                        .name(ur.getRole().getName())
                        .description(ur.getRole().getDescription())
                        .build())
                .collect(Collectors.toList());

        var profile = customerProfileRepository.findByUserId(user.getId());
        String fullName = profile.map(p -> p.getFullName()).orElse("");
        String phone = profile.map(p -> p.getPhone()).orElse("");
        String address = profile.map(p -> p.getAddress()).orElse("");

        return UserDetailResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .roles(roles)
                .fullName(fullName)
                .phone(phone)
                .address(address)
                .build();
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        boolean isDisabling = (request.getStatus() == UserStatus.LOCKED || request.getStatus() == UserStatus.DISABLED);

        if (isDisabling) {
            boolean isAdmin = userRoleRepository.findByUserId(id).stream()
                    .anyMatch(ur -> "ROLE_ADMIN".equals(ur.getRole().getName()));
            
            if (isAdmin) {
                long activeAdmins = userRepository.countActiveAdmins();
                if (activeAdmins <= 1 && user.getIsActive()) {
                    throw new BusinessException("Cannot disable or lock the last active admin account.");
                }
            }
        }

        user.setIsActive(!isDisabling);
        userRepository.save(user);
    }
}
