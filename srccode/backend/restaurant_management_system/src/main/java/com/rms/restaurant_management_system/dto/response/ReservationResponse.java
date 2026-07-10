package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReservationResponse {

    private Long reservationId;

    private String reservationCode;

    private Long userId;

    private String customerName;

    private String phone;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    private Integer guests;

    private ReservationStatus status;

    private String assignedTable;

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<ReservationPreOrderItemResponse> preOrderItems;
}
