package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationExpiryJob {
    private final ReservationRepository reservationRepository;

    @Scheduled(fixedDelayString = "${app.reservation-expiry-delay-ms:300000}")
    @Transactional
    public void expirePastReservations() {
        List<Reservation> expired = reservationRepository.findByStatusInAndEndAtBefore(
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED), LocalDateTime.now());
        for (Reservation reservation : expired) {
            reservation.setStatus(reservation.getStatus() == ReservationStatus.CONFIRMED
                    ? ReservationStatus.NO_SHOW : ReservationStatus.CANCELLED);
            reservation.setAssignedTable(null);
            reservation.setAssignedTableId(null);
        }
    }
}
