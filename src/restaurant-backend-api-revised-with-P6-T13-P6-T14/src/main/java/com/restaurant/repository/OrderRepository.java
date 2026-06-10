package com.restaurant.repository;

import com.restaurant.entity.Order;
import com.restaurant.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer_UserIdOrderByCreatedAtDesc(Long customerId);
    List<Order> findByOrderStatus(OrderStatus status);
    List<Order> findAllByOrderByCreatedAtDesc();
}
