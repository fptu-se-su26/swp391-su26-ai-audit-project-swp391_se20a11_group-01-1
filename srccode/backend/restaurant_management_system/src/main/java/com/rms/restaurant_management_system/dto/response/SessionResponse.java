package com.rms.restaurant_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionResponse {
    private Long userId;
    private String username;
    private String email;
    private String roleName;
    private String token;
    private long expiresIn;
}
