package com.restaurant.management.service;

import com.restaurant.management.dto.auth.AuthResponse;
import com.restaurant.management.dto.auth.CurrentUserResponse;
import com.restaurant.management.dto.auth.LoginRequest;
import com.restaurant.management.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    CurrentUserResponse getCurrentUser(String email);
}
