package com.restaurant.management.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long paidOrders;
    private Long pendingOrders;
    private Long cancelledOrders;
    
    private BigDecimal todayRevenue;
    private Long todayOrders;
    
    private Long totalReservations;
    private Long pendingReservations;
    
    private Long availableTables;
}
