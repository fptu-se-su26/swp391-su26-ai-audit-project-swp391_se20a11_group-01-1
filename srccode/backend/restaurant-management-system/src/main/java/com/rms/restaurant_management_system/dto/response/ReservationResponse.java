package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.ReservationStatus;
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
    private Long userId;
    private String userEmail;
    private Long tableId;
    private Integer tableNumber;
    private LocalDateTime reservationTime;
    private Integer guestCount;
    private ReservationStatus status;
    private String note;
    private LocalDateTime createdAt;
}
