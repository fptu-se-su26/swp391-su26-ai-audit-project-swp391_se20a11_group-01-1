package com.restaurant.management.service;

import com.restaurant.management.dto.auth.AuthResponse;
import com.restaurant.management.dto.auth.CurrentUserResponse;
import com.restaurant.management.dto.auth.LoginRequest;
import com.restaurant.management.dto.auth.RegisterRequest;
import com.restaurant.management.dto.auth.UserSummaryResponse;
import com.restaurant.management.entity.auth.CustomerProfile;
import com.restaurant.management.entity.auth.Role;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.auth.UserRole;
import com.restaurant.management.exception.ConflictException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.CustomerProfileRepository;
import com.restaurant.management.repository.RoleRepository;
import com.restaurant.management.repository.UserRepository;
import com.restaurant.management.repository.UserRoleRepository;
import com.restaurant.management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already taken!");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username is already taken!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        user = userRepository.save(user);

        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_CUSTOMER not found. Please seed the database."));

        UserRole userRoleMap = new UserRole(user, userRole);
        userRoleRepository.save(userRoleMap);

        CustomerProfile profile = CustomerProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        customerProfileRepository.save(profile);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserSummaryResponse userSummary = UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(List.of("ROLE_CUSTOMER"))
                .build();

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .user(userSummary)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        UserSummaryResponse userSummary = UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .user(userSummary)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        String fullName = "";
        String phone = "";
        
        var profile = customerProfileRepository.findByUserId(user.getId());
        if (profile.isPresent()) {
            fullName = profile.get().getFullName();
            phone = profile.get().getPhone();
        }

        List<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        return CurrentUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(fullName)
                .phone(phone)
                .roles(roles)
                .build();
    }
}
