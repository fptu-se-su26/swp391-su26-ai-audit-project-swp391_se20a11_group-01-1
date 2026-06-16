package com.restaurant.management.service;

import com.restaurant.management.dto.coupon.CouponRequest;
import com.restaurant.management.dto.coupon.CouponResponse;
import com.restaurant.management.dto.coupon.CouponValidationRequest;
import com.restaurant.management.dto.coupon.CouponValidationResponse;
import com.restaurant.management.dto.coupon.UpdateCouponStatusRequest;
import com.restaurant.management.entity.coupon.Coupon;
import com.restaurant.management.entity.coupon.CouponStatus;
import com.restaurant.management.entity.coupon.DiscountType;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public CouponValidationResponse validateCoupon(CouponValidationRequest request) {
        Coupon coupon = couponRepository.findByCode(request.getCode())
                .orElseThrow(() -> new BadRequestException("Coupon not found"));

        if (coupon.getDeletedAt() != null) {
            throw new BadRequestException("Coupon not found");
        }

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new BadRequestException("Coupon is " + coupon.getStatus().name().toLowerCase());
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            throw new BadRequestException("Coupon is expired or not yet started");
        }

        if (coupon.getMinOrderValue().compareTo(request.getOrderAmount()) > 0) {
            throw new BadRequestException("Minimum order amount not reached");
        }

        if (coupon.getUsageLimit() > 0) {
            int usedCount = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
            if (usedCount >= coupon.getUsageLimit()) {
                throw new BadRequestException("Coupon usage limit exceeded");
            }
        }

        BigDecimal discountAmount = calculateDiscount(coupon, request.getOrderAmount());
        BigDecimal finalAmount = request.getOrderAmount().subtract(discountAmount);
        
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        return CouponValidationResponse.builder()
                .valid(true)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .couponCode(coupon.getCode())
                .build();
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            BigDecimal percentage = coupon.getDiscountValue().divide(BigDecimal.valueOf(100));
            discount = orderAmount.multiply(percentage);
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        }
        return discount;
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        Optional<Coupon> existing = couponRepository.findByCode(request.getCode());
        if (existing.isPresent()) {
            throw new BadRequestException("Coupon code already exists");
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .status(request.getStatus())
                .build();

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        if (!coupon.getCode().equals(request.getCode())) {
            Optional<Coupon> existing = couponRepository.findByCode(request.getCode());
            if (existing.isPresent()) {
                throw new BadRequestException("Coupon code already exists");
            }
        }

        coupon.setCode(request.getCode());
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setStatus(request.getStatus());

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CouponResponse updateStatus(Long id, UpdateCouponStatusRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        coupon.setStatus(request.getStatus());
        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        return mapToResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCoupons() {
        return couponRepository.findAll().stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount() != null ? coupon.getUsedCount() : 0)
                .status(coupon.getStatus())
                .build();
    }
}
