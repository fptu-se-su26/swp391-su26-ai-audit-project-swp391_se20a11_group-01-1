package com.restaurant.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data @Builder
public class UserAdminResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
