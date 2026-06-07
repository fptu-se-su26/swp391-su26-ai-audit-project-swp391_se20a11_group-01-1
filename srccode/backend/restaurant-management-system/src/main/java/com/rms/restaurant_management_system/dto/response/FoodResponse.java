package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.FoodStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FoodResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private FoodStatus status;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
