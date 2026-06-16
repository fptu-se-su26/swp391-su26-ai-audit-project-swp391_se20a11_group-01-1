package com.restaurant.management.service;

import com.restaurant.management.dto.admin.RoleResponse;
import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRoles();
}
