package com.restaurant.repository;

import com.restaurant.entity.Order;
import com.restaurant.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface OrderReportRepository extends JpaRepository<Order, Long> {
    long countByOrderStatus(OrderStatus status);

    @Query("select count(o) from Order o where o.orderStatus = com.restaurant.model.OrderStatus.COMPLETED and (:fromDate is null or o.createdAt >= :fromDate) and (:toDate is null or o.createdAt <= :toDate)")
    long countCompletedBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}
