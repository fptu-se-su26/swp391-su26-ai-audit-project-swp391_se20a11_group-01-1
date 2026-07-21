package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.ChangePasswordRequest;
import com.rms.restaurant_management_system.dto.request.LoginRequest;
import com.rms.restaurant_management_system.dto.request.RegisterRequest;
import com.rms.restaurant_management_system.dto.response.AuthResponse;
import com.rms.restaurant_management_system.dto.response.AuthResult;
import com.rms.restaurant_management_system.dto.response.SessionResponse;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.impl.SessionService;
import com.rms.restaurant_management_system.service.interfaces.AuthService;
import com.rms.restaurant_management_system.dto.request.ForgotPasswordRequest;
import com.rms.restaurant_management_system.dto.request.ResetPasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import com.rms.restaurant_management_system.security.LoginAttemptService;
import com.rms.restaurant_management_system.error.AuthenticationRequiredException;
import com.rms.restaurant_management_system.error.ErrorCode;
import com.rms.restaurant_management_system.error.ResourceNotFoundException;
import com.rms.restaurant_management_system.security.SessionRejectedException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Value("${app.jwt-refresh-days:30}")
    private long refreshDays;

    @Value("${app.cookie-secure:false}")
    private boolean secureCookie;

    @PostMapping("/register")
    public SessionResponse register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest,
                                    HttpServletResponse response) {
        AuthResponse registered = authService.register(request);
        return beginSession(registered.getEmail(), httpRequest, response);
    }

    @PostMapping("/login")
    public SessionResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest,
                                 HttpServletResponse response) {
        String attemptKey = httpRequest.getRemoteAddr() + ":" + request.getEmail().trim().toLowerCase();
        loginAttemptService.assertAllowed(attemptKey);
        try {
            AuthResponse loggedIn = authService.login(request);
            loginAttemptService.succeeded(attemptKey);
            return beginSession(loggedIn.getEmail(), httpRequest, response);
        } catch (AuthenticationRequiredException exception) {
            loginAttemptService.failed(attemptKey);
            throw exception;
        }
    }

    @PostMapping("/refresh")
    public SessionResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        AuthResult result = sessionService.rotate(readRefreshCookie(request), request.getRemoteAddr(),
                request.getHeader("User-Agent"));
        writeRefreshCookie(response, result.refreshToken());
        return result.session();
    }

    @GetMapping("/session")
    public SessionResponse session(Authentication authentication) {
        User user = requireUser(authentication);
        return new SessionResponse(user.getUserId(), user.getUsername(), user.getEmail(),
                user.getRole().getRoleName(), null, 0);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        sessionService.revoke(readRefreshCookie(request));
        clearRefreshCookie(response);
    }

    @PostMapping("/logout-all")
    public void logoutAll(Authentication authentication, HttpServletResponse response) {
        sessionService.revokeAll(requireUser(authentication));
        clearRefreshCookie(response);
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        request.setEmail(requireUser(authentication).getEmail());
        authService.changePassword(request);
        return "Change password successfully";
    }
        @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return "Reset password successfully";
    }

    private SessionResponse beginSession(String email, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        AuthResult result = sessionService.start(user, request.getRemoteAddr(), request.getHeader("User-Agent"));
        writeRefreshCookie(response, result.refreshToken());
        return result.session();
    }

    private User requireUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationRequiredException(ErrorCode.AUTHENTICATION_REQUIRED, "Chưa đăng nhập");
        }
        return user;
    }

    private String readRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) return cookie.getValue();
            }
        }
        throw new SessionRejectedException("Refresh token không tồn tại");
    }

    private void writeRefreshCookie(HttpServletResponse response, String token) {
        response.addHeader("Set-Cookie", refreshCookie(token, Duration.ofDays(refreshDays)).toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie", refreshCookie("", Duration.ZERO).toString());
    }

    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from("refresh_token", value)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(maxAge)
                .build();
    }
}
