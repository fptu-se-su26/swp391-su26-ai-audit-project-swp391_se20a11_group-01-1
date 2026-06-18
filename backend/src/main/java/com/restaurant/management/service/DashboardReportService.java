package com.restaurant.management.service;

import com.restaurant.management.dto.report.DashboardSummaryResponse;
import com.restaurant.management.dto.report.OrderReportResponse;
import com.restaurant.management.dto.report.ReservationReportResponse;
import com.restaurant.management.dto.report.RevenueReportResponse;
import com.restaurant.management.dto.report.TopFoodResponse;
import com.restaurant.management.entity.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardReportService {
    DashboardSummaryResponse getDashboardSummary();
    RevenueReportResponse getRevenueReport(LocalDateTime fromDate, LocalDateTime toDate);
    OrderReportResponse getOrderReport(LocalDateTime fromDate, LocalDateTime toDate, OrderStatus status);
    List<TopFoodResponse> getTopFoods(LocalDateTime fromDate, LocalDateTime toDate, Integer limit);
    ReservationReportResponse getReservationReport(LocalDateTime fromDate, LocalDateTime toDate);
}
