package com.restaurant.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class DashboardResponse {
    private long totalUsers;
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private long paidPayments;
    private BigDecimal totalRevenue;
    private long availableFoods;
    private long activeCoupons;
}
