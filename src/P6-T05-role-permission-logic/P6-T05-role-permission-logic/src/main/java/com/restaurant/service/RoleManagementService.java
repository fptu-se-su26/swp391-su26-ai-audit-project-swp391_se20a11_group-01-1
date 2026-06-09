package com.restaurant.service;

import com.restaurant.constant.AppRole;
import com.restaurant.entity.Role;
import com.restaurant.entity.User;
import com.restaurant.repository.RoleRepository;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public User assignRole(Long userId, AppRole appRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Role role = roleRepository.findByRoleName(appRole.name())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + appRole.name()));

        user.setRole(role);
        return userRepository.save(user);
    }
}
