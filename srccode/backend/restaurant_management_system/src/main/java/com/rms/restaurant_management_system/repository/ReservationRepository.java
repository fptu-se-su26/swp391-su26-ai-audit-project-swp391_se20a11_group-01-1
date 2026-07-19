package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByReservationId(Long reservationId);

    Optional<Reservation> findByReservationCode(String reservationCode);

    List<Reservation> findAllByOrderByCreatedAtDesc();

    List<Reservation> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    List<Reservation> findByStatusOrderByCreatedAtDesc(ReservationStatus status);

    List<Reservation> findByReservationDateOrderByReservationTimeAsc(LocalDate reservationDate);
}
