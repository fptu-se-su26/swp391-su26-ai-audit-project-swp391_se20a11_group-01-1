package com.restaurant.management.controller.admin;

import com.restaurant.management.dto.coupon.CouponRequest;
import com.restaurant.management.dto.coupon.CouponResponse;
import com.restaurant.management.dto.coupon.UpdateCouponStatusRequest;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons() {
        List<CouponResponse> coupons = couponService.getCoupons();
        return ResponseEntity.ok(ApiResponse.success(coupons, "Coupons fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCoupon(@PathVariable Long id) {
        CouponResponse coupon = couponService.getCoupon(id);
        return ResponseEntity.ok(ApiResponse.success(coupon, "Coupon fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse coupon = couponService.createCoupon(request);
        return ResponseEntity.ok(ApiResponse.success(coupon, "Coupon created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request) {
        CouponResponse coupon = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.success(coupon, "Coupon updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CouponResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCouponStatusRequest request) {
        CouponResponse coupon = couponService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(coupon, "Coupon status updated successfully"));
    }
}
