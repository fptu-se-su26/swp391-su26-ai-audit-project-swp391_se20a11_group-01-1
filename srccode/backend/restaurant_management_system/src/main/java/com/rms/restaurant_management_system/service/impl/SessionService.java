package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.response.AuthResult;
import com.rms.restaurant_management_system.dto.response.SessionResponse;
import com.rms.restaurant_management_system.entity.RefreshToken;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.RefreshTokenRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.security.JwtUtil;
import com.rms.restaurant_management_system.security.SessionRejectedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.jwt-refresh-days:30}")
    private long refreshDays;

    @Transactional
    public AuthResult start(User user, String ip, String userAgent) {
        return issue(user, UUID.randomUUID().toString(), ip, userAgent);
    }

    @Transactional(noRollbackFor = SessionRejectedException.class)
    public AuthResult rotate(String rawToken, String ip, String userAgent) {
        RefreshToken current = refreshTokenRepository.findLockedByTokenHash(hash(rawToken))
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));
        LocalDateTime now = LocalDateTime.now();
        if (current.getRevokedAt() != null) {
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw new SessionRejectedException("Refresh token has been revoked");
        }
        if (current.getExpiresAt().isBefore(now)) {
            current.setRevokedAt(now);
            throw new SessionRejectedException("Refresh token has expired");
        }
        User user = userRepository.findById(current.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw new SessionRejectedException("Account is inactive");
        }
        current.setLastUsedAt(now);
        current.setRevokedAt(now);
        refreshTokenRepository.save(current);
        return issue(user, current.getFamilyId(), ip, userAgent);
    }

    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(token -> {
            if (token.getRevokedAt() == null) token.setRevokedAt(LocalDateTime.now());
        });
    }

    @Transactional
    public void revokeAll(User user) {
        refreshTokenRepository.revokeAllByUserId(user.getUserId(), LocalDateTime.now());
    }

    private AuthResult issue(User user, String familyId, String ip, String userAgent) {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user).tokenHash(hash(raw)).familyId(familyId)
                .createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusDays(refreshDays))
                .ipAddress(ip).userAgent(trim(userAgent, 500)).build());
        String access = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName(), user.getTokenVersion());
        return new AuthResult(new SessionResponse(user.getUserId(), user.getUsername(), user.getEmail(),
                user.getRole().getRoleName(), access, jwtUtil.getAccessTokenSeconds()), raw);
    }

    private String hash(String raw) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String trim(String value, int max) {
        return value == null ? null : value.substring(0, Math.min(value.length(), max));
    }
}
