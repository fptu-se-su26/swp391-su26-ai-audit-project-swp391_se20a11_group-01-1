package com.restaurant.repository;

import com.restaurant.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH oi.foodItem
            LEFT JOIN FETCH o.customer
            LEFT JOIN FETCH o.restaurantTable
            LEFT JOIN FETCH o.coupon
            WHERE o.id = :orderId
            """)
    Optional<Order> findOrderDetailById(@Param("orderId") Long orderId);
}
