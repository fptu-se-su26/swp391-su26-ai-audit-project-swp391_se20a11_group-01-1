package com.restaurant.dto.admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Set;

@Data
public class RoleAssignmentRequest {
    @NotEmpty
    private Set<String> roles;
}
