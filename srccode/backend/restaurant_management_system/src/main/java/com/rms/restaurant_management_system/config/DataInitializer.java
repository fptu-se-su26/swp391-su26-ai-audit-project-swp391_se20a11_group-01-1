
package com.rms.restaurant_management_system.config;

import com.rms.restaurant_management_system.entity.Role;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.RoleRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("CUSTOMER");
        createRoleIfNotExists("STAFF");
        createRoleIfNotExists("KITCHEN");

        createUserIfNotExists(
                "admin",
                "admin@gmail.com",
                "123456",
                "ADMIN"
        );

        createUserIfNotExists(
                "staff",
                "staff@gmail.com",
                "123456",
                "STAFF"
        );

        createUserIfNotExists(
                "kitchen",
                "kitchen@gmail.com",
                "123456",
                "KITCHEN"
        );

        createUserIfNotExists(
                "customer",
                "customer@gmail.com",
                "123456",
                "CUSTOMER"
        );
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .roleName(roleName)
                    .isActive(true)
                    .build();

            roleRepository.save(role);
        }
    }

    private void createUserIfNotExists(
            String username,
            String email,
            String password,
            String roleName
    ) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);
    }
}

