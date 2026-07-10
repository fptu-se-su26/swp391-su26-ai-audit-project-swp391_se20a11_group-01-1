package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationCode(String reservationCode);

    List<Reservation> findAllByOrderByReservationDateDescReservationTimeDesc();

    List<Reservation> findByStatusOrderByReservationDateAscReservationTimeAsc(ReservationStatus status);

    List<Reservation> findByUserUserIdOrderByReservationDateDescReservationTimeDesc(Long userId);

    List<Reservation> findByReservationDateOrderByReservationTimeAsc(LocalDate reservationDate);
}
