package com.rms.restaurant_management_system.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String address;
    private String avatarUrl;
}
