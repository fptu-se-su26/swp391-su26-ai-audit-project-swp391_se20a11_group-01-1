package com.restaurant.management.service;

import com.restaurant.management.dto.admin.RoleResponse;
import com.restaurant.management.entity.auth.Role;
import com.restaurant.management.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role roleAdmin;
    private Role roleCustomer;

    @BeforeEach
    void setUp() {
        roleAdmin = Role.builder().id(1L).name("ROLE_ADMIN").build();
        roleCustomer = Role.builder().id(2L).name("ROLE_CUSTOMER").build();
    }

    @Test
    void testGetAllRoles_Success() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(roleAdmin, roleCustomer));

        List<RoleResponse> roles = roleService.getAllRoles();

        assertEquals(2, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0).getName());
        assertEquals("ROLE_CUSTOMER", roles.get(1).getName());
    }
}
