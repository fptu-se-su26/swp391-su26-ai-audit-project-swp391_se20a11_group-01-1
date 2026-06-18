package com.restaurant.management.repository;

import com.restaurant.management.entity.payment.Payment;
import com.restaurant.management.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    boolean existsByOrderIdAndPaymentStatus(Long orderId, PaymentStatus status);
    Optional<Payment> findByOrderIdAndPaymentStatus(Long orderId, PaymentStatus status);
    Optional<Payment> findByPaymentReference(String paymentReference);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'PAID'")
    java.math.BigDecimal sumTotalRevenue();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.createdAt >= :start AND p.createdAt <= :end")
    java.math.BigDecimal sumRevenueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.createdAt >= :start AND p.createdAt <= :end")
    Long countPaidPaymentsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.createdAt >= :start AND p.createdAt <= :end")
    List<Payment> findPaidPaymentsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
