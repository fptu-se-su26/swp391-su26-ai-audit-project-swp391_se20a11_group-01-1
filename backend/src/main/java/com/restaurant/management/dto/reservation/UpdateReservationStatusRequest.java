package com.restaurant.management.dto.reservation;

import com.restaurant.management.entity.reservation.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReservationStatusRequest {
    @NotNull(message = "Status is required")
    private ReservationStatus status;
}
