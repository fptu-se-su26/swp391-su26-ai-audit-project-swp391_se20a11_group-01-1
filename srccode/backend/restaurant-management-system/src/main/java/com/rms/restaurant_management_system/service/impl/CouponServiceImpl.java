package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.CouponRequest;
import com.rms.restaurant_management_system.dto.response.CouponResponse;
import com.rms.restaurant_management_system.dto.response.CouponValidationResponse;
import com.rms.restaurant_management_system.entity.Coupon;
import com.rms.restaurant_management_system.enums.DiscountType;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.CouponRepository;
import com.rms.restaurant_management_system.service.interfaces.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse getCouponById(Long id) {
        return mapToResponse(getCouponEntity(id));
    }

    @Override
    public CouponResponse createCoupon(CouponRequest request) {
        String code = request.getCode().toUpperCase().trim();
        
        if (couponRepository.existsByCode(code)) {
            throw new BadRequestException("Coupon code already exists");
        }

        Coupon coupon = Coupon.builder()
                .code(code)
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .expirationDate(request.getExpirationDate())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        return mapToResponse(savedCoupon);
    }

    @Override
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = getCouponEntity(id);
        String code = request.getCode().toUpperCase().trim();

        if (couponRepository.existsByCodeAndIdNot(code, id)) {
            throw new BadRequestException("Coupon code already exists");
        }

        coupon.setCode(code);
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setExpirationDate(request.getExpirationDate());
        if (request.getIsActive() != null) {
            coupon.setIsActive(request.getIsActive());
        }
        coupon.setUsageLimit(request.getUsageLimit());

        Coupon updatedCoupon = couponRepository.save(coupon);
        return mapToResponse(updatedCoupon);
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponEntity(id);
        couponRepository.delete(coupon);
    }

    @Override
    public CouponValidationResponse validateAndCalculateDiscount(String code, BigDecimal orderTotal) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase().trim()).orElse(null);

        if (coupon == null) {
            return new CouponValidationResponse(false, "Invalid coupon code", BigDecimal.ZERO, orderTotal);
        }

        if (!coupon.getIsActive()) {
            return new CouponValidationResponse(false, "Coupon is inactive", BigDecimal.ZERO, orderTotal);
        }

        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
            return new CouponValidationResponse(false, "Coupon has expired", BigDecimal.ZERO, orderTotal);
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return new CouponValidationResponse(false, "Coupon usage limit reached", BigDecimal.ZERO, orderTotal);
        }

        if (coupon.getMinOrderValue() != null && orderTotal.compareTo(coupon.getMinOrderValue()) < 0) {
            return new CouponValidationResponse(false, "Order total does not meet minimum requirement", BigDecimal.ZERO, orderTotal);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discountAmount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = orderTotal.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        }

        // Ensure discount is not greater than order total
        if (discountAmount.compareTo(orderTotal) > 0) {
            discountAmount = orderTotal;
        }

        BigDecimal finalTotal = orderTotal.subtract(discountAmount);

        return new CouponValidationResponse(true, "Coupon applied successfully", discountAmount, finalTotal);
    }

    private Coupon getCouponEntity(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .expirationDate(coupon.getExpirationDate())
                .isActive(coupon.getIsActive())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}
