package com.restaurant.management.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    private String description;
    
    @Size(max = 255, message = "Image URL must be less than 255 characters")
    private String imageUrl;
}
