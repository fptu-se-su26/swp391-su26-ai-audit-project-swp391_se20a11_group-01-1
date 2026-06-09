package com.restaurant.dto.admin;

import com.restaurant.constant.AppRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleAssignmentRequest {
    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private AppRole role;
}
