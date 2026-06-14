package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Payment;
import com.rms.restaurant_management_system.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);
    Optional<Payment> findByOrderId(Long orderId);
}
