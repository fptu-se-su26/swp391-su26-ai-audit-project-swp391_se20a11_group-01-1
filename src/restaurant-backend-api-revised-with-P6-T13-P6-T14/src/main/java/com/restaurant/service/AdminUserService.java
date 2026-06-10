package com.restaurant.service;

import com.restaurant.dto.admin.*;
import com.restaurant.entity.Role;
import com.restaurant.entity.User;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.UserStatus;
import com.restaurant.repository.RoleRepository;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<UserAdminResponse> getUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserAdminResponse getUser(Long userId) {
        return toResponse(getUserEntity(userId));
    }

    @Transactional
    public UserAdminResponse updateUser(Long userId, UserAdminRequest request) {
        User user = getUserEntity(userId);
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getStatus() != null) user.setStatus(parseUserStatus(request.getStatus()));
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) throw new BadRequestException("Email already exists");
            user.setEmail(request.getEmail());
        }
        if (request.getRoles() != null) user.setRoles(loadRoles(request.getRoles()));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserAdminResponse assignRoles(Long userId, RoleAssignmentRequest request) {
        User user = getUserEntity(userId);
        user.setRoles(loadRoles(request.getRoles()));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserAdminResponse lockUser(Long userId) {
        User user = getUserEntity(userId);
        user.setStatus(UserStatus.LOCKED);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserAdminResponse activateUser(Long userId) {
        User user = getUserEntity(userId);
        user.setStatus(UserStatus.ACTIVE);
        return toResponse(userRepository.save(user));
    }

    private Set<Role> loadRoles(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            String normalized = roleName.trim().toUpperCase();
            Role role = roleRepository.findByRoleName(normalized)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + normalized));
            roles.add(role);
        }
        return roles;
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserStatus parseUserStatus(String value) {
        try { return UserStatus.valueOf(value.trim().toUpperCase()); }
        catch (Exception e) { throw new BadRequestException("Invalid user status: " + value); }
    }

    private UserAdminResponse toResponse(User user) {
        return UserAdminResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
