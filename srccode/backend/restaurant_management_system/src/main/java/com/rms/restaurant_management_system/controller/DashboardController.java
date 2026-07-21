package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.response.DashboardSummaryResponse;
import com.rms.restaurant_management_system.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
}
