package com.rms.restaurant_management_system.dto.request;

import com.rms.restaurant_management_system.enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.00", message = "Minimum order value cannot be negative")
    private BigDecimal minOrderValue;

    @DecimalMin(value = "0.01", message = "Maximum discount amount must be greater than 0")
    private BigDecimal maxDiscountAmount;

    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expirationDate;

    private Boolean isActive;

    private Integer usageLimit;
}
