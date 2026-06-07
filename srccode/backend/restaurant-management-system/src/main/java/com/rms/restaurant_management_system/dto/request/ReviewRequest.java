package com.rms.restaurant_management_system.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "Food ID is required")
    private Long foodId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating min is 1")
    @Max(value = 5, message = "Rating max is 5")
    private Integer rating;

    private String comment;
}
