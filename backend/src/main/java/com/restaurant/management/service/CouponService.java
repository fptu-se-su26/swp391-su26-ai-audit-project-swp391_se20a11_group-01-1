package com.restaurant.management.service;

import com.restaurant.management.dto.coupon.CouponRequest;
import com.restaurant.management.dto.coupon.CouponResponse;
import com.restaurant.management.dto.coupon.CouponValidationRequest;
import com.restaurant.management.dto.coupon.CouponValidationResponse;
import com.restaurant.management.dto.coupon.UpdateCouponStatusRequest;

import java.util.List;

public interface CouponService {
    CouponValidationResponse validateCoupon(CouponValidationRequest request);
    CouponResponse createCoupon(CouponRequest request);
    CouponResponse updateCoupon(Long id, CouponRequest request);
    CouponResponse updateStatus(Long id, UpdateCouponStatusRequest request);
    CouponResponse getCoupon(Long id);
    List<CouponResponse> getCoupons();
}
