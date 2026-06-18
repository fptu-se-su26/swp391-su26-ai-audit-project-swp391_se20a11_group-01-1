package com.restaurant.management.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class OrderReportResponse {
    private Long totalOrders;
    private Map<String, Long> byStatus;
    private BigDecimal totalAmount;
}
