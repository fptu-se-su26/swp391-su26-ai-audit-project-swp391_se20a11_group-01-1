package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;

    private Long foodId;

    private String foodName;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;

    private String imageUrl;

    private String emoji;

    private String note;

    private OrderItemStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime statusUpdatedAt;
}
