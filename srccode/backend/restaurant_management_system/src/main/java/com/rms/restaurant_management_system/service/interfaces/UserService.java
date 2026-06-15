package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.response.UserResponse;

public interface UserService {

    UserResponse getProfileByEmail(String email);
}