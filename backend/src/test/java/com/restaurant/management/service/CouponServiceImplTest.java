package com.restaurant.management.service;

import com.restaurant.management.dto.coupon.CouponRequest;
import com.restaurant.management.dto.coupon.CouponResponse;
import com.restaurant.management.dto.coupon.CouponValidationRequest;
import com.restaurant.management.dto.coupon.CouponValidationResponse;
import com.restaurant.management.entity.coupon.Coupon;
import com.restaurant.management.entity.coupon.CouponStatus;
import com.restaurant.management.entity.coupon.DiscountType;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon coupon;
    private CouponValidationRequest validationRequest;

    @BeforeEach
    void setUp() {
        coupon = Coupon.builder()
                .id(1L)
                .code("SUMMER50")
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("50000"))
                .minOrderValue(new BigDecimal("200000"))
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .usageLimit(100)
                .usedCount(10)
                .status(CouponStatus.ACTIVE)
                .build();

        validationRequest = new CouponValidationRequest();
        validationRequest.setCode("SUMMER50");
        validationRequest.setOrderAmount(new BigDecimal("300000"));
    }

    @Test
    void testValidateCoupon_Success() {
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.of(coupon));

        CouponValidationResponse response = couponService.validateCoupon(validationRequest);

        assertTrue(response.isValid());
        assertEquals(new BigDecimal("50000"), response.getDiscountAmount());
        assertEquals(new BigDecimal("250000"), response.getFinalAmount());
        assertEquals("SUMMER50", response.getCouponCode());
    }

    @Test
    void testValidateCoupon_NotFound() {
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> couponService.validateCoupon(validationRequest));
    }

    @Test
    void testValidateCoupon_Inactive() {
        coupon.setStatus(CouponStatus.INACTIVE);
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.of(coupon));

        assertThrows(BadRequestException.class, () -> couponService.validateCoupon(validationRequest));
    }

    @Test
    void testValidateCoupon_Expired() {
        coupon.setEndDate(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.of(coupon));

        assertThrows(BadRequestException.class, () -> couponService.validateCoupon(validationRequest));
    }

    @Test
    void testValidateCoupon_UsageExceeded() {
        coupon.setUsageLimit(10);
        coupon.setUsedCount(10);
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.of(coupon));

        assertThrows(BadRequestException.class, () -> couponService.validateCoupon(validationRequest));
    }

    @Test
    void testValidateCoupon_MinimumAmountNotReached() {
        validationRequest.setOrderAmount(new BigDecimal("100000")); // min is 200,000
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.of(coupon));

        assertThrows(BadRequestException.class, () -> couponService.validateCoupon(validationRequest));
    }

    @Test
    void testCreateCoupon_DuplicateCode() {
        CouponRequest request = new CouponRequest();
        request.setCode("SUMMER50");
        
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.of(coupon));

        assertThrows(BadRequestException.class, () -> couponService.createCoupon(request));
    }

    @Test
    void testValidateCoupon_PercentageDiscount() {
        coupon.setDiscountType(DiscountType.PERCENTAGE);
        coupon.setDiscountValue(new BigDecimal("10")); // 10%
        coupon.setMaxDiscountAmount(new BigDecimal("20000"));
        
        when(couponRepository.findByCode("SUMMER50")).thenReturn(Optional.of(coupon));

        CouponValidationResponse response = couponService.validateCoupon(validationRequest);

        // 10% of 300000 = 30000, but max is 20000
        assertEquals(new BigDecimal("20000"), response.getDiscountAmount());
        assertEquals(new BigDecimal("280000"), response.getFinalAmount());
    }
}
