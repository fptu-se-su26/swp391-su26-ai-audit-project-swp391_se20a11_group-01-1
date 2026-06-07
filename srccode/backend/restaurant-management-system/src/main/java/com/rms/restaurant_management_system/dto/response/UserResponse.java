package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String avatarUrl;
    private UserStatus status;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
