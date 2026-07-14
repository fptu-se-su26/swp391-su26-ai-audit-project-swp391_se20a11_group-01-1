package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.response.StaffCustomerResponse;
import com.rms.restaurant_management_system.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getProfileByEmail(String email);

    List<UserResponse> getAllUsers();

    List<StaffCustomerResponse> getStaffCustomers();
}