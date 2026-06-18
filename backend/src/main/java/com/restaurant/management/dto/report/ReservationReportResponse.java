package com.restaurant.management.dto.report;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ReservationReportResponse {
    private Long totalReservations;
    private Map<String, Long> byStatus;
    private Long totalGuests;
}
