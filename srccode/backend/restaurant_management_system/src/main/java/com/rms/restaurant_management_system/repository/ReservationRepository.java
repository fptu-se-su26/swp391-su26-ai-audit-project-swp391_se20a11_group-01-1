package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByReservationId(Long reservationId);

    Optional<Reservation> findByReservationCode(String reservationCode);

    List<Reservation> findAllByOrderByCreatedAtDesc();

    List<Reservation> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    List<Reservation> findByStatusOrderByCreatedAtDesc(ReservationStatus status);

    List<Reservation> findByReservationDateOrderByReservationTimeAsc(LocalDate reservationDate);

    boolean existsByReservationIdAndUserUserId(Long reservationId, Long userId);

    @Query("""
            select count(r) from Reservation r
            where r.assignedTableId = :tableId
              and r.status in (com.rms.restaurant_management_system.enums.ReservationStatus.PENDING,
                               com.rms.restaurant_management_system.enums.ReservationStatus.CONFIRMED)
              and r.startAt < :endAt and r.endAt > :startAt
              and (:excludeId is null or r.reservationId <> :excludeId)
            """)
    long countOverlapping(@Param("tableId") Long tableId, @Param("startAt") LocalDateTime startAt,
                          @Param("endAt") LocalDateTime endAt, @Param("excludeId") Long excludeId);

    List<Reservation> findByStatusInAndEndAtBefore(List<ReservationStatus> statuses, LocalDateTime cutoff);
}
