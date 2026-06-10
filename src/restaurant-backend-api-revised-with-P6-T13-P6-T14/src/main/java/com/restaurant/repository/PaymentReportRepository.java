package com.restaurant.repository;

import com.restaurant.entity.Payment;
import com.restaurant.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentReportRepository extends JpaRepository<Payment, Long> {
    long countByPaymentStatus(PaymentStatus status);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paymentStatus = com.restaurant.model.PaymentStatus.PAID")
    BigDecimal sumPaidAmount();

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paymentStatus = com.restaurant.model.PaymentStatus.PAID and (:fromDate is null or p.paidAt >= :fromDate) and (:toDate is null or p.paidAt <= :toDate)")
    BigDecimal sumPaidAmountBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("select count(p) from Payment p where p.paymentStatus = com.restaurant.model.PaymentStatus.PAID and (:fromDate is null or p.paidAt >= :fromDate) and (:toDate is null or p.paidAt <= :toDate)")
    long countPaidBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}
