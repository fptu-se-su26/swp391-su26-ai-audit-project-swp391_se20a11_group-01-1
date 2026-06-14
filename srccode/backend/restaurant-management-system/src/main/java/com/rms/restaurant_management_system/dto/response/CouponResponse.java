package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.DiscountType;
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
public class CouponResponse {
    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime expirationDate;
    private Boolean isActive;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime createdAt;
}
