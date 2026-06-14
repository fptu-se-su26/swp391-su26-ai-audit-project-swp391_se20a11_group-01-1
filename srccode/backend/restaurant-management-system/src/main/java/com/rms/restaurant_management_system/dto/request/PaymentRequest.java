package com.rms.restaurant_management_system.dto.request;

import com.rms.restaurant_management_system.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    // For testing/mocking failures
    private boolean simulateFailure;
}
