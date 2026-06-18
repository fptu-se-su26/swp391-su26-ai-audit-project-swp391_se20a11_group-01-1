package com.restaurant.management.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RevenueReportResponse {
    private BigDecimal totalRevenue;
    private Long totalPaidPayments;
    private Long totalInvoices;
    private List<DailyRevenue> dailyRevenue;
}
