package com.restaurant.repository;

import com.restaurant.entity.Payment;
import com.restaurant.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrder_OrderIdOrderByCreatedAtDesc(Long orderId);
    List<Payment> findByOrder_OrderId(Long orderId);
    List<Payment> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);
}
