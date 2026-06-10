package com.restaurant.dto.admin;

import jakarta.validation.constraints.Email;
import lombok.Data;
import java.util.Set;

@Data
public class UserAdminRequest {
    private String fullName;
    @Email private String email;
    private String phone;
    private String address;
    private String status;
    private Set<String> roles;
}
