package com.restaurant.security;

import com.restaurant.constant.Permission;
import com.restaurant.service.AuthorizationService;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final AuthorizationService authorizationService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return checkPermission(authentication, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return checkPermission(authentication, permission);
    }

    private boolean checkPermission(Authentication authentication, Object permission) {
        if (permission == null) {
            return false;
        }

        try {
            Permission parsedPermission = Permission.valueOf(permission.toString().toUpperCase());
            return authorizationService.hasPermission(authentication, parsedPermission);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
