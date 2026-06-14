package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.CouponRequest;
import com.rms.restaurant_management_system.dto.response.CouponResponse;
import com.rms.restaurant_management_system.dto.response.CouponValidationResponse;
import com.rms.restaurant_management_system.entity.Coupon;
import com.rms.restaurant_management_system.enums.DiscountType;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.repository.CouponRepository;
import com.rms.restaurant_management_system.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon couponPercentage;
    private Coupon couponFixed;

    @BeforeEach
    void setUp() {
        couponPercentage = Coupon.builder()
                .id(1L)
                .code("SUMMER20")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("20.00"))
                .minOrderValue(new BigDecimal("100.00"))
                .maxDiscountAmount(new BigDecimal("50.00"))
                .expirationDate(LocalDateTime.now().plusDays(10))
                .isActive(true)
                .usageLimit(100)
                .usedCount(0)
                .build();

        couponFixed = Coupon.builder()
                .id(2L)
                .code("MINUS10")
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("10.00"))
                .minOrderValue(new BigDecimal("50.00"))
                .isActive(true)
                .usedCount(0)
                .build();
    }

    @Test
    void createCoupon_Success() {
        CouponRequest request = CouponRequest.builder()
                .code("new50")
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("50.00"))
                .build();

        when(couponRepository.existsByCode("NEW50")).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(i -> i.getArguments()[0]);

        CouponResponse response = couponService.createCoupon(request);

        assertEquals("NEW50", response.getCode());
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void createCoupon_DuplicateCode_ShouldThrowException() {
        CouponRequest request = CouponRequest.builder().code("SUMMER20").build();
        when(couponRepository.existsByCode("SUMMER20")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> couponService.createCoupon(request));
    }

    @Test
    void validateAndCalculateDiscount_ValidPercentage_ShouldCalculateCorrectly() {
        when(couponRepository.findByCode("SUMMER20")).thenReturn(Optional.of(couponPercentage));

        // 20% of 200 = 40. Less than max 50.
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("summer20", new BigDecimal("200.00"));

        assertTrue(response.isValid());
        assertEquals(0, new BigDecimal("40.00").compareTo(response.getDiscountAmount()));
        assertEquals(0, new BigDecimal("160.00").compareTo(response.getFinalTotal()));
    }

    @Test
    void validateAndCalculateDiscount_ValidPercentage_ShouldCapAtMaxDiscount() {
        when(couponRepository.findByCode("SUMMER20")).thenReturn(Optional.of(couponPercentage));

        // 20% of 500 = 100. Capped at 50.
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("SUMMER20", new BigDecimal("500.00"));

        assertTrue(response.isValid());
        assertEquals(0, new BigDecimal("50.00").compareTo(response.getDiscountAmount()));
        assertEquals(0, new BigDecimal("450.00").compareTo(response.getFinalTotal()));
    }

    @Test
    void validateAndCalculateDiscount_ValidFixed_ShouldCalculateCorrectly() {
        when(couponRepository.findByCode("MINUS10")).thenReturn(Optional.of(couponFixed));

        CouponValidationResponse response = couponService.validateAndCalculateDiscount("MINUS10", new BigDecimal("100.00"));

        assertTrue(response.isValid());
        assertEquals(0, new BigDecimal("10.00").compareTo(response.getDiscountAmount()));
        assertEquals(0, new BigDecimal("90.00").compareTo(response.getFinalTotal()));
    }

    @Test
    void validateAndCalculateDiscount_OrderLessThanMinOrder_ShouldBeInvalid() {
        when(couponRepository.findByCode("SUMMER20")).thenReturn(Optional.of(couponPercentage));

        // min order is 100
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("SUMMER20", new BigDecimal("50.00"));

        assertFalse(response.isValid());
        assertEquals("Order total does not meet minimum requirement", response.getMessage());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getDiscountAmount()));
    }

    @Test
    void validateAndCalculateDiscount_Expired_ShouldBeInvalid() {
        couponPercentage.setExpirationDate(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByCode("SUMMER20")).thenReturn(Optional.of(couponPercentage));

        CouponValidationResponse response = couponService.validateAndCalculateDiscount("SUMMER20", new BigDecimal("200.00"));

        assertFalse(response.isValid());
        assertEquals("Coupon has expired", response.getMessage());
    }

    @Test
    void validateAndCalculateDiscount_UsageLimitReached_ShouldBeInvalid() {
        couponPercentage.setUsedCount(100);
        when(couponRepository.findByCode("SUMMER20")).thenReturn(Optional.of(couponPercentage));

        CouponValidationResponse response = couponService.validateAndCalculateDiscount("SUMMER20", new BigDecimal("200.00"));

        assertFalse(response.isValid());
        assertEquals("Coupon usage limit reached", response.getMessage());
    }

    @Test
    void validateAndCalculateDiscount_FixedDiscountGreaterThanOrder_ShouldCapAtOrderTotal() {
        when(couponRepository.findByCode("MINUS10")).thenReturn(Optional.of(couponFixed));

        // minOrder is 50, but let's say order is 50, discount is 10.
        // let's temporarily set min order to 0
        couponFixed.setMinOrderValue(BigDecimal.ZERO);

        CouponValidationResponse response = couponService.validateAndCalculateDiscount("MINUS10", new BigDecimal("5.00"));

        assertTrue(response.isValid());
        assertEquals(0, new BigDecimal("5.00").compareTo(response.getDiscountAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getFinalTotal()));
    }
}
