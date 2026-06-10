package com.restaurant.dto.staff;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffCreateOrderRequest {
    @NotNull(message = "customerId is required because orders.customer_id is NOT NULL in schema.sql")
    private Long customerId;

    /** Optional tableId for dine-in operation. Current schema does not store table_id in orders,
     * so StaffService only updates the table status and appends table info to order note.
     */
    private Long tableId;

    private String orderType; // DINE_IN, TAKEAWAY, DELIVERY
    private String couponCode;
    private String note;

    /** Optional initial payment method. If provided, system creates a PENDING payment record. */
    private String paymentMethod; // CASH, ONLINE, QR, CARD, BANK_TRANSFER

    @Valid
    @NotEmpty(message = "items must contain at least one food")
    private List<StaffOrderItemRequest> items;
}
