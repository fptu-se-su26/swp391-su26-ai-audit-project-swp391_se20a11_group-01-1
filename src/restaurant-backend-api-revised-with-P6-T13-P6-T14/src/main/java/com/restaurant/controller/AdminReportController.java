package com.restaurant.controller;

import com.restaurant.common.ApiResponse;
import com.restaurant.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {
    private final AdminReportService adminReportService;

    @GetMapping("/dashboard")
    public ApiResponse<?> dashboard() { return ApiResponse.ok(adminReportService.dashboard()); }

    @GetMapping("/revenue")
    public ApiResponse<?> revenue(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.ok(adminReportService.revenue(from, to));
    }

    @GetMapping("/best-selling-foods")
    public ApiResponse<?> bestSellingFoods(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(adminReportService.bestSellingFoods(limit));
    }
}
