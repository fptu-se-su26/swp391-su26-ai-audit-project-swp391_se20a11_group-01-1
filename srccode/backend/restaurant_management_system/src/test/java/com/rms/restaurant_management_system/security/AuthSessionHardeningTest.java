package com.rms.restaurant_management_system.security;

import com.rms.restaurant_management_system.dto.request.ResetPasswordRequest;
import com.rms.restaurant_management_system.entity.*;
import com.rms.restaurant_management_system.repository.*;
import com.rms.restaurant_management_system.service.impl.AuthServiceImpl;
import com.rms.restaurant_management_system.service.impl.SessionService;
import com.rms.restaurant_management_system.service.interfaces.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthSessionHardeningTest {
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EmailService emailService;
    @Mock JwtUtil jwtUtil;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @InjectMocks AuthServiceImpl authService;

    @Test
    void invalidOtpRecordsFailureOutsideRollbackPath() {
        User user = activeUser();
        PasswordResetToken token = resetToken("123456");
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc("user@example.com"))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword(resetRequest("000000")))
                .hasMessage("OTP is invalid or expired");
        verify(passwordResetTokenRepository).recordFailedAttempt(token.getId(), 5);
        verify(passwordResetTokenRepository, never()).consumeIfValid(anyLong(), anyString(), any(), anyInt());
        verify(userRepository, never()).save(any());
    }

    @Test
    void validOtpIsAtomicallyConsumedBeforePasswordChange() {
        User user = activeUser();
        PasswordResetToken token = resetToken("123456");
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc("user@example.com"))
                .thenReturn(Optional.of(token));
        when(passwordResetTokenRepository.consumeIfValid(eq(token.getId()), eq(token.getTokenHash()), any(), eq(5)))
                .thenReturn(1);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded");

        authService.resetPassword(resetRequest("123456"));

        assertThat(user.getPasswordHash()).isEqualTo("encoded");
        assertThat(user.getTokenVersion()).isEqualTo(1);
        verify(refreshTokenRepository).revokeAllByUserId(eq(user.getUserId()), any());
    }

    @Test
    void alreadyConsumedOtpCannotChangePassword() {
        User user = activeUser();
        PasswordResetToken token = resetToken("123456");
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc("user@example.com"))
                .thenReturn(Optional.of(token));
        when(passwordResetTokenRepository.consumeIfValid(eq(token.getId()), anyString(), any(), eq(5)))
                .thenReturn(0);

        assertThatThrownBy(() -> authService.resetPassword(resetRequest("123456")))
                .hasMessage("OTP is invalid or expired");
        verify(userRepository, never()).save(any());
    }

    @Test
    void rejectedSessionChangesAreConfiguredToCommit() throws Exception {
        Transactional annotation = SessionService.class
                .getMethod("rotate", String.class, String.class, String.class)
                .getAnnotation(Transactional.class);
        assertThat(annotation.noRollbackFor()).contains(SessionRejectedException.class);
    }

    @Test
    void loginLimiterBlocksAfterFiveFailuresAndSuccessClearsState() {
        LoginAttemptService limiter = new LoginAttemptService();
        String key = "127.0.0.1:user@example.com";
        for (int attempt = 0; attempt < 5; attempt++) {
            limiter.assertAllowed(key);
            limiter.failed(key);
        }
        assertThatThrownBy(() -> limiter.assertAllowed(key)).hasMessageContaining("Too many login attempts");
        limiter.succeeded(key);
        assertThatCode(() -> limiter.assertAllowed(key)).doesNotThrowAnyException();
    }

    private User activeUser() {
        return User.builder().userId(7L).email("user@example.com").username("user")
                .passwordHash("old").tokenVersion(0).isActive(true)
                .role(Role.builder().roleName("CUSTOMER").build()).build();
    }

    private PasswordResetToken resetToken(String otp) {
        return PasswordResetToken.builder().id(10L).email("user@example.com").tokenHash(hash(otp))
                .createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusMinutes(5))
                .failedAttempts(0).build();
    }

    private ResetPasswordRequest resetRequest(String otp) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("user@example.com");
        request.setOtp(otp);
        request.setNewPassword("new-password");
        return request;
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
