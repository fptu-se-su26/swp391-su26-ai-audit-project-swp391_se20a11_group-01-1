package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.ChangePasswordRequest;
import com.rms.restaurant_management_system.dto.request.LoginRequest;
import com.rms.restaurant_management_system.dto.request.RegisterRequest;
import com.rms.restaurant_management_system.dto.response.AuthResponse;
import com.rms.restaurant_management_system.entity.Role;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.repository.RoleRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.impl.AuthServiceImpl;
import com.rms.restaurant_management_system.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EmailService emailService;
    AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(userRepository, roleRepository, passwordEncoder, emailService);
    }

    @Test
    void registerCreatesActiveCustomerWithEncodedPassword() {
        RegisterRequest request = register("customer", "customer@example.com", "secret12");
        Role customer = Role.builder().roleId(4L).roleName("CUSTOMER").build();
        when(roleRepository.findByRoleName("CUSTOMER")).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("secret12")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(10L);
            return user;
        });

        AuthResponse response = service.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertAll(
                () -> assertEquals("bcrypt-hash", captor.getValue().getPasswordHash()),
                () -> assertEquals("CUSTOMER", captor.getValue().getRole().getRoleName()),
                () -> assertTrue(captor.getValue().getIsActive()),
                () -> assertEquals(10L, response.getUserId()),
                () -> assertEquals("CUSTOMER", response.getRoleName())
        );
    }

    @Test
    void registerRejectsDuplicateEmailBeforeWriting() {
        RegisterRequest request = register("customer", "duplicate@example.com", "secret12");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        RuntimeException error = assertThrows(RuntimeException.class, () -> service.register(request));
        assertEquals("Email already exists", error.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRejectsDuplicateUsername() {
        RegisterRequest request = register("duplicate", "new@example.com", "secret12");
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> service.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginReturnsUserForCorrectPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("customer@example.com");
        request.setPassword("secret12");
        User user = user(1L, true, "hash");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret12", "hash")).thenReturn(true);
        AuthResponse response = service.login(request);
        assertEquals(1L, response.getUserId());
        assertEquals("CUSTOMER", response.getRoleName());
    }

    @Test
    void loginRejectsUnknownEmailInactiveUserAndWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("x@example.com");
        request.setPassword("wrong");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.login(request));

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user(1L, false, "hash")));
        assertThrows(RuntimeException.class, () -> service.login(request));

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user(1L, true, "hash")));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.login(request));
    }

    @Test
    void changePasswordEncodesAndPersistsNewPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("customer@example.com");
        request.setOldPassword("oldpass");
        request.setNewPassword("newpass");
        User user = user(1L, true, "old-hash");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldpass", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("new-hash");
        service.changePassword(request);
        assertEquals("new-hash", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsWrongOldPasswordAndShortNewPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("customer@example.com");
        request.setOldPassword("wrong");
        request.setNewPassword("123456");
        User user = user(1L, true, "old-hash");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.changePassword(request));

        request.setOldPassword("correct");
        request.setNewPassword("12345");
        when(passwordEncoder.matches("correct", "old-hash")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> service.changePassword(request));
        verify(userRepository, never()).save(any());
    }

    private RegisterRequest register(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username); request.setEmail(email); request.setPassword(password);
        return request;
    }

    private User user(Long id, boolean active, String hash) {
        return User.builder().userId(id).username("customer").email("customer@example.com")
                .passwordHash(hash).isActive(active)
                .role(Role.builder().roleName("CUSTOMER").build()).build();
    }
}
