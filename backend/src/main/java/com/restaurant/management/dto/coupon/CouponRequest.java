package com.restaurant.management.dto.coupon;

import com.restaurant.management.entity.coupon.CouponStatus;
import com.restaurant.management.entity.coupon.DiscountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {
    @NotBlank(message = "Code is required")
    private String code;

    private String name;
    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Min(value = 0, message = "Discount value must be at least 0")
    private BigDecimal discountValue;

    @NotNull(message = "Minimum order value is required")
    @Min(value = 0, message = "Minimum order value must be at least 0")
    private BigDecimal minOrderValue;

    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "Usage limit is required")
    @Min(value = 0, message = "Usage limit must be at least 0")
    private Integer usageLimit;

    @NotNull(message = "Status is required")
    private CouponStatus status;
}
