package com.rms.restaurant_management_system.dto.response;

public record AuthResult(SessionResponse session, String refreshToken) {
}
