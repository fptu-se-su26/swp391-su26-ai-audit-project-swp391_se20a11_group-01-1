package com.rms.restaurant_management_system.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ReservationRequest {

    private Long userId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;

    @NotNull(message = "Reservation time is required")
    private LocalTime reservationTime;

    @NotNull(message = "Guests is required")
    @Min(value = 1, message = "Guests must be at least 1")
    private Integer guests;

    private String note;

    @Valid
    private List<ReservationPreOrderItemRequest> preOrderItems;
}
