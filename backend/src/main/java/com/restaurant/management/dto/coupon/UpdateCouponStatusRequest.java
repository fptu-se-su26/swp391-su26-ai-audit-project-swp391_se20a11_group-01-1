package com.restaurant.management.dto.coupon;

import com.restaurant.management.entity.coupon.CouponStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCouponStatusRequest {
    @NotNull(message = "Status is required")
    private CouponStatus status;
}
