package com.restaurant.management.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopFoodResponse {
    private String foodName;
    private Long totalQuantity;
    private BigDecimal totalRevenue;
}
