package com.restaurant.service;

import com.restaurant.constant.AppRole;
import com.restaurant.constant.Permission;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final Map<AppRole, Set<Permission>> rolePermissions = new EnumMap<>(AppRole.class);

    public AuthorizationService() {
        rolePermissions.put(AppRole.CUSTOMER, EnumSet.of(
                Permission.MENU_READ,
                Permission.ORDER_CREATE,
                Permission.ORDER_READ_OWN
        ));

        rolePermissions.put(AppRole.STAFF, EnumSet.of(
                Permission.MENU_READ,
                Permission.ORDER_CREATE,
                Permission.TABLE_MANAGE,
                Permission.PAYMENT_CONFIRM
        ));

        rolePermissions.put(AppRole.KITCHEN, EnumSet.of(
                Permission.KITCHEN_ORDER_READ,
                Permission.KITCHEN_STATUS_UPDATE
        ));

        rolePermissions.put(AppRole.ADMIN, EnumSet.allOf(Permission.class));
    }

    public boolean hasRole(Authentication authentication, AppRole role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role.asSpringRole()));
    }

    public boolean hasAnyRole(Authentication authentication, AppRole... roles) {
        for (AppRole role : roles) {
            if (hasRole(authentication, role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(Authentication authentication, Permission permission) {
        AppRole currentRole = getCurrentRole(authentication);
        if (currentRole == null) {
            return false;
        }
        return rolePermissions.getOrDefault(currentRole, Set.of()).contains(permission);
    }

    public AppRole getCurrentRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::normalizeRoleName)
                .map(this::safeParseRole)
                .filter(role -> role != null)
                .findFirst()
                .orElse(null);
    }

    public Set<Permission> getPermissionsByRole(AppRole role) {
        return rolePermissions.getOrDefault(role, Set.of());
    }

    private String normalizeRoleName(String authority) {
        return authority == null ? "" : authority.toUpperCase().replace("ROLE_", "");
    }

    private AppRole safeParseRole(String roleName) {
        try {
            return AppRole.valueOf(roleName);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
