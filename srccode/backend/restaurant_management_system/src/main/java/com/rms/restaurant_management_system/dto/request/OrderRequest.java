package com.rms.restaurant_management_system.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String note;

    @Valid
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;
}