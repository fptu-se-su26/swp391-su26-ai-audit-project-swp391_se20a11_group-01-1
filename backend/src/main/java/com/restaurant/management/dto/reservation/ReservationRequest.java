package com.restaurant.management.dto.reservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    private Long tableId; // Optional, if null, auto-assign

    @NotNull(message = "Reservation time is required")
    @Future(message = "Reservation time must be in the future")
    private LocalDateTime reservationTime;

    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "Guest count must be at least 1")
    private Integer guestCount;

    private String specialRequest;
}
