package com.restaurant.management.service;

import com.restaurant.management.dto.admin.UpdateUserStatusRequest;
import com.restaurant.management.dto.admin.UserDetailResponse;
import com.restaurant.management.dto.admin.UserListResponse;
import com.restaurant.management.dto.admin.UserStatus;
import com.restaurant.management.entity.auth.Role;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.auth.UserRole;
import com.restaurant.management.exception.BusinessException;
import com.restaurant.management.repository.CustomerProfileRepository;
import com.restaurant.management.repository.UserRepository;
import com.restaurant.management.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private User user;
    private Role adminRole;
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("admin")
                .email("admin@test.com")
                .isActive(true)
                .build();
        user.setId(1L);

        adminRole = Role.builder().id(1L).name("ROLE_ADMIN").build();
        
        userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(adminRole);
    }

    @Test
    void testGetUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.searchUsers(any(), any(), any(), eq(pageable))).thenReturn(usersPage);
        when(userRoleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(userRole));
        when(customerProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Page<UserListResponse> result = userManagementService.getUsers(null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("admin", result.getContent().get(0).getUsername());
        assertTrue(result.getContent().get(0).getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    void testGetUserDetail_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(userRole));
        when(customerProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        UserDetailResponse result = userManagementService.getUserDetail(1L);

        assertEquals("admin", result.getUsername());
        assertEquals(1, result.getRoles().size());
        assertEquals("ROLE_ADMIN", result.getRoles().get(0).getName());
    }

    @Test
    void testUpdateUserStatus_LockUser_Success() {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setStatus(UserStatus.LOCKED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(userRole));
        when(userRepository.countActiveAdmins()).thenReturn(2L); // 2 admins active, safe to lock one

        userManagementService.updateUserStatus(1L, request);

        assertFalse(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserStatus_LockLastAdmin_ThrowsException() {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setStatus(UserStatus.DISABLED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(userRole));
        when(userRepository.countActiveAdmins()).thenReturn(1L); // Only 1 admin active!

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userManagementService.updateUserStatus(1L, request);
        });

        assertEquals("Cannot disable or lock the last active admin account.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
