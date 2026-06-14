package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserEmail(String email);

    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId " +
           "AND r.status IN :statuses " +
           "AND r.reservationTime >= :startTime AND r.reservationTime <= :endTime")
    List<Reservation> findConflictingReservations(@Param("tableId") Long tableId, 
                                                  @Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime,
                                                  @Param("statuses") List<com.rms.restaurant_management_system.enums.ReservationStatus> statuses);
}
