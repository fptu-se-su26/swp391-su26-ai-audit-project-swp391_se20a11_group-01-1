package com.rms.restaurant_management_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private Long userId;
    private String userFullName;
    private Long foodId;
    private String foodName;
    private LocalDateTime createdAt;
}
