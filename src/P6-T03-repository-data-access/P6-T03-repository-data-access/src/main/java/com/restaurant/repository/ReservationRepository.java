package com.restaurant.repository;

import com.restaurant.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByCustomer_IdOrderByReservationTimeDesc(Long customerId);

    List<Reservation> findByRestaurantTable_Id(Long tableId);

    List<Reservation> findByReservationStatus(String reservationStatus);

    List<Reservation> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end);
}
