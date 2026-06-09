package com.restaurant.controller;

import com.restaurant.constant.AppRole;
import com.restaurant.dto.common.ApiResponse;
import com.restaurant.service.AuthorizationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessCheckController {

    private final AuthorizationService authorizationService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentAccess(Authentication authentication) {
        AppRole role = authorizationService.getCurrentRole(authentication);

        Map<String, Object> data = Map.of(
                "username", authentication.getName(),
                "role", role == null ? "UNKNOWN" : role.name(),
                "permissions", role == null ? "[]" : authorizationService.getPermissionsByRole(role)
        );

        return ResponseEntity.ok(ApiResponse.ok("Current access information", data));
    }
}
