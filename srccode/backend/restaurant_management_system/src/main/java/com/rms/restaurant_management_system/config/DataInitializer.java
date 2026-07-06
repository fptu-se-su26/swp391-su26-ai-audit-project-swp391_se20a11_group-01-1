package com.rms.restaurant_management_system.config;

import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.entity.Role;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
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
    private final RestaurantTableRepository restaurantTableRepository;
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

        createDefaultTables();
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

    private void createDefaultTables() {
        createTableIfNotExists("Bàn 1", 2);
        createTableIfNotExists("Bàn 2", 2);
        createTableIfNotExists("Bàn 3", 4);
        createTableIfNotExists("Bàn 4", 4);
        createTableIfNotExists("Bàn 5", 4);
        createTableIfNotExists("Bàn 6", 4);
        createTableIfNotExists("Bàn 7", 6);
        createTableIfNotExists("Bàn 8", 6);
        createTableIfNotExists("Bàn 9", 6);
        createTableIfNotExists("Bàn 10", 6);
        createTableIfNotExists("Bàn 11", 8);
        createTableIfNotExists("Bàn 12", 8);
        createTableIfNotExists("Bàn 13", 2);
        createTableIfNotExists("Bàn 14", 2);
        createTableIfNotExists("Bàn 15", 4);
        createTableIfNotExists("Bàn 16", 4);
        createTableIfNotExists("Bàn 17", 6);
        createTableIfNotExists("Bàn 18", 6);
        createTableIfNotExists("Bàn 19", 8);
        createTableIfNotExists("Bàn 20", 10);
    }

    private void createTableIfNotExists(String tableName, Integer capacity) {
        if (restaurantTableRepository.existsByTableName(tableName)) {
            return;
        }

        RestaurantTable table = RestaurantTable.builder()
                .tableName(tableName)
                .capacity(capacity)
                .status(TableStatus.EMPTY)
                .currentOrderCode(null)
                .reservedBy(null)
                .mergedInto(null)
                .mergedWith(null)
                .isActive(true)
                .build();

        restaurantTableRepository.save(table);
    }
}