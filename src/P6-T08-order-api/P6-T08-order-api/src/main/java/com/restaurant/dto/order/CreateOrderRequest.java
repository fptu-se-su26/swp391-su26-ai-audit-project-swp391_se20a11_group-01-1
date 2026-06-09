package com.restaurant.dto.order;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    private Long tableId;

    @Size(max = 50, message = "Coupon code must be less than 50 characters")
    private String couponCode;

    @Size(max = 255, message = "Note must be less than 255 characters")
    private String note;
}
