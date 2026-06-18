package com.restaurant.management.repository;

import com.restaurant.management.entity.payment.Payment;
import com.restaurant.management.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    boolean existsByOrderIdAndPaymentStatus(Long orderId, PaymentStatus status);
    Optional<Payment> findByOrderIdAndPaymentStatus(Long orderId, PaymentStatus status);
}
