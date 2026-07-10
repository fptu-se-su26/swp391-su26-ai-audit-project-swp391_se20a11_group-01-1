package com.rms.restaurant_management_system.dto.request;

import com.rms.restaurant_management_system.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    private Long userId;

    private Long tableId;

    private String customerName;

    private String customerPhone;

    private String note;

    private PaymentMethod paymentMethod;

    @Valid
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;
}
