package com.restaurant.controller;
import com.restaurant.common.ApiResponse; import com.restaurant.dto.auth.*; import com.restaurant.service.AuthService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/auth") @RequiredArgsConstructor
public class AuthController {
 private final AuthService authService;
 @PostMapping("/register") public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request){ return ApiResponse.ok("Register successful", authService.register(request)); }
 @PostMapping("/login") public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request){ return ApiResponse.ok("Login successful", authService.login(request)); }
 @GetMapping("/me") public ApiResponse<UserProfileResponse> me(){ return ApiResponse.ok("Current user", authService.me()); }
}
