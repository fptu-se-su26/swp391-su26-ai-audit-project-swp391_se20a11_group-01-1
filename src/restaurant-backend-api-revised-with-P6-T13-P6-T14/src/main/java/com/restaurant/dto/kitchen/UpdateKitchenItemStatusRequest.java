package com.restaurant.dto.kitchen;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateKitchenItemStatusRequest {
    @NotBlank
    private String itemStatus;
    private String note;
}
