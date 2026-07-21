package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.*;
import com.rms.restaurant_management_system.dto.response.AuthResponse;
import com.rms.restaurant_management_system.entity.PasswordResetToken;
import com.rms.restaurant_management_system.entity.Role;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.*;
import com.rms.restaurant_management_system.security.JwtUtil;
import com.rms.restaurant_management_system.service.interfaces.AuthService;
import com.rms.restaurant_management_system.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_RESET_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) throw new RuntimeException("Email already exists");
        if (userRepository.existsByUsername(username)) throw new RuntimeException("Username already exists");

        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));
        User user = userRepository.save(User.builder()
                .username(username).email(email).passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(customerRole).isActive(true).build());
        return authResponse(user, "Register successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("Email or password is incorrect"));
        if (!Boolean.TRUE.equals(user.getIsActive())
                || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Email or password is incorrect");
        }
        return authResponse(user, "Login successfully");
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }
        validateNewPassword(request.getNewPassword());
        updatePasswordAndRevoke(user, request.getNewPassword());
    }

    @Override
    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) return genericResetMessage();

        PasswordResetToken latest = passwordResetTokenRepository
                .findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc(email).orElse(null);
        if (latest != null && latest.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
            return genericResetMessage();
        }

        String otp = String.valueOf(100000 + SECURE_RANDOM.nextInt(900000));
        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .email(email).tokenHash(hash(otp)).createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5)).build());
        emailService.sendOtpEmail(user.getEmail(), otp);
        return genericResetMessage();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .filter(candidate -> Boolean.TRUE.equals(candidate.getIsActive()))
                .orElseThrow(() -> new RuntimeException("OTP is invalid or expired"));
        PasswordResetToken token = passwordResetTokenRepository
                .findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP is invalid or expired"));

        if (LocalDateTime.now().isAfter(token.getExpiresAt()) || token.getFailedAttempts() >= MAX_RESET_ATTEMPTS) {
            token.setUsedAt(LocalDateTime.now());
            throw new RuntimeException("OTP is invalid or expired");
        }
        if (!MessageDigest.isEqual(hash(request.getOtp()).getBytes(StandardCharsets.UTF_8),
                token.getTokenHash().getBytes(StandardCharsets.UTF_8))) {
            token.setFailedAttempts(token.getFailedAttempts() + 1);
            throw new RuntimeException("OTP is invalid or expired");
        }

        validateNewPassword(request.getNewPassword());
        token.setUsedAt(LocalDateTime.now());
        updatePasswordAndRevoke(user, request.getNewPassword());
    }

    private AuthResponse authResponse(User user, String message) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName(), user.getTokenVersion());
        return new AuthResponse(user.getUserId(), user.getUsername(), user.getEmail(),
                user.getRole().getRoleName(), token, message);
    }

    private void validateNewPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters");
        }
    }

    private void updatePasswordAndRevoke(User user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setTokenVersion((user.getTokenVersion() == null ? 0 : user.getTokenVersion()) + 1);
        userRepository.save(user);
        refreshTokenRepository.revokeAllByUserId(user.getUserId(), LocalDateTime.now());
    }

    private String genericResetMessage() {
        return "If the account exists, an OTP has been sent";
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
