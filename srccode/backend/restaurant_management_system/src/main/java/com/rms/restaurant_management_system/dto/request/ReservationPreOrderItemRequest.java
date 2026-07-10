package com.rms.restaurant_management_system.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReservationPreOrderItemRequest {

    private Long foodId;

    private String foodName;

    private Integer quantity;

    private BigDecimal unitPrice;
}
