package com.restaurant.management.dto.coupon;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponValidationRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Order amount is required")
    @Min(value = 0, message = "Order amount must be at least 0")
    private BigDecimal orderAmount;
}
