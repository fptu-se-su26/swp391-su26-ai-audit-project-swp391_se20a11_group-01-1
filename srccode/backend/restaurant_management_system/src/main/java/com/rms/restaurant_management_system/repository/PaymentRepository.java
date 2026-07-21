package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderOrderId(Long orderId);

    Optional<Payment> findByPayosOrderCode(Long payosOrderCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findLockedByOrderOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findLockedByPayosOrderCode(Long payosOrderCode);

    boolean existsByOrderOrderId(Long orderId);

    List<Payment> findAllByOrderByCreatedAtDesc();
}
