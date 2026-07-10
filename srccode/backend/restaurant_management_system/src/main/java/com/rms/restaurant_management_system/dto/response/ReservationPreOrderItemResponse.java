package com.rms.restaurant_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ReservationPreOrderItemResponse {

    private Long preOrderItemId;

    private Long foodId;

    private String foodName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}
