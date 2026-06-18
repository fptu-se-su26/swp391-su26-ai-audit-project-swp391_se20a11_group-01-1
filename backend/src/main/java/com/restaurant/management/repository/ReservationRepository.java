package com.restaurant.management.repository;

import com.restaurant.management.entity.reservation.Reservation;
import com.restaurant.management.entity.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByCustomerIdAndDeletedAtIsNull(Long customerId);
    
    List<Reservation> findAllByDeletedAtIsNull();

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.table.id = :tableId " +
           "AND r.status IN :statuses " +
           "AND r.deletedAt IS NULL " +
           "AND r.reservationTime > :startTimeMinus2Hours " +
           "AND r.reservationTime < :endTime")
    boolean existsOverlapReservation(
            @Param("tableId") Long tableId, 
            @Param("startTimeMinus2Hours") LocalDateTime startTimeMinus2Hours, 
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<ReservationStatus> statuses);

    @Query("SELECT COUNT(r) FROM Reservation r")
    Long countTotalReservations();

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = 'PENDING'")
    Long countPendingReservations();

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.createdAt >= :start AND r.createdAt <= :end")
    Long countReservationsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT r.status, COUNT(r) FROM Reservation r WHERE r.createdAt >= :start AND r.createdAt <= :end GROUP BY r.status")
    List<Object[]> countGroupedByStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(r.guestCount) FROM Reservation r WHERE r.createdAt >= :start AND r.createdAt <= :end")
    Long sumGuestsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
