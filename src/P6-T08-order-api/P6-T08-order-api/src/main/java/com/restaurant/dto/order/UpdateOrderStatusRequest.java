package com.restaurant.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotBlank(message = "Order status is required")
    private String orderStatus;
}
