package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.response.UserResponse;
import com.rms.restaurant_management_system.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public UserResponse getProfileByEmail(@RequestParam String email) {
        return userService.getProfileByEmail(email);
    }
}