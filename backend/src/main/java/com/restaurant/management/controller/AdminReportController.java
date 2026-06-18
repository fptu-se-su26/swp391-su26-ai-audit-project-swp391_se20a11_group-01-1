package com.restaurant.management.controller;

import com.restaurant.management.dto.report.OrderReportResponse;
import com.restaurant.management.dto.report.ReservationReportResponse;
import com.restaurant.management.dto.report.RevenueReportResponse;
import com.restaurant.management.dto.report.TopFoodResponse;
import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.service.DashboardReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final DashboardReportService dashboardReportService;

    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RevenueReportResponse> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(dashboardReportService.getRevenueReport(fromDate, toDate));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderReportResponse> getOrderReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(dashboardReportService.getOrderReport(fromDate, toDate, status));
    }

    @GetMapping("/top-foods")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TopFoodResponse>> getTopFoodsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(dashboardReportService.getTopFoods(fromDate, toDate, limit));
    }

    @GetMapping("/reservations")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ReservationReportResponse> getReservationReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(dashboardReportService.getReservationReport(fromDate, toDate));
    }
}
