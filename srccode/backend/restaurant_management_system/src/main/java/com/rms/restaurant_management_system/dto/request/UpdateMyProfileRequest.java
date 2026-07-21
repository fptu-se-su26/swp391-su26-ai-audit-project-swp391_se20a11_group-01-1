package com.rms.restaurant_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateMyProfileRequest {
    @NotBlank @Size(max = 150)
    private String fullName;
    @Pattern(regexp = "^$|^[0-9+() .-]{8,30}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @Size(max = 500)
    private String avatarUrl;
}
