package com.restaurant.management.service;

import com.restaurant.management.dto.auth.AuthResponse;
import com.restaurant.management.dto.auth.LoginRequest;
import com.restaurant.management.dto.auth.RegisterRequest;
import com.restaurant.management.entity.auth.Role;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.exception.ConflictException;
import com.restaurant.management.repository.CustomerProfileRepository;
import com.restaurant.management.repository.RoleRepository;
import com.restaurant.management.repository.UserRepository;
import com.restaurant.management.repository.UserRoleRepository;
import com.restaurant.management.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CustomerProfileRepository customerProfileRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("Password@123");
        registerRequest.setFullName("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("Password@123");

        user = User.builder()
                .username("testuser")
                .email("test@test.com")
                .passwordHash("hashedPass")
                .isActive(true)
                .build();
        user.setId(1L);

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_CUSTOMER");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPass");
        when(userRepository.save(any())).thenReturn(user);
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(role));
        
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getAccessToken());
        assertEquals("test@test.com", response.getUser().getEmail());
    }

    @Test
    void testRegister_DuplicateEmail() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testLogin_Success() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.generateToken(any())).thenReturn("fake-jwt-token");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getAccessToken());
    }

    @Test
    void testLogin_WrongPassword() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }
}
