package com.restaurant.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class RevenueResponse {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private BigDecimal totalRevenue;
    private long paidPaymentCount;
    private long completedOrderCount;
}
