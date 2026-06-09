package com.restaurant.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrderSummaryResponse {

    private Long orderId;
    private String orderCode;
    private String orderType;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
}
