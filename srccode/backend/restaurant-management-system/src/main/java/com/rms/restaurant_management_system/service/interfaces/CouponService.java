package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.CouponRequest;
import com.rms.restaurant_management_system.dto.response.CouponResponse;
import com.rms.restaurant_management_system.dto.response.CouponValidationResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<CouponResponse> getAllCoupons();
    CouponResponse getCouponById(Long id);
    CouponResponse createCoupon(CouponRequest request);
    CouponResponse updateCoupon(Long id, CouponRequest request);
    void deleteCoupon(Long id);
    CouponValidationResponse validateAndCalculateDiscount(String code, BigDecimal orderTotal);
}
