package com.restaurant.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDetailResponse {

    private Long orderId;
    private String orderCode;
    private String orderType;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;

    private Long customerId;
    private String customerName;

    private Long tableId;
    private String tableNumber;

    private String couponCode;
    private List<OrderItemResponse> items;
}
