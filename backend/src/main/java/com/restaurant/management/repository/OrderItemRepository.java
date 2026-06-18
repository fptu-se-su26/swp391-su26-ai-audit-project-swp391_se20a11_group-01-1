package com.restaurant.management.repository;

import com.restaurant.management.entity.order.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT i.foodItem.name, SUM(i.quantity), SUM(i.quantity * i.unitPrice) " +
           "FROM OrderItem i WHERE i.order.createdAt >= :start AND i.order.createdAt <= :end " +
           "GROUP BY i.foodItem.name ORDER BY SUM(i.quantity) DESC")
    List<Object[]> getTopFoods(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
}
