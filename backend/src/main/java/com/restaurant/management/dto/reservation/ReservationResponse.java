package com.restaurant.management.dto.reservation;

import com.restaurant.management.entity.reservation.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private Long customerId;
    private Long tableId;
    private LocalDateTime reservationTime;
    private Integer guestCount;
    private ReservationStatus status;
    private String specialRequest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
