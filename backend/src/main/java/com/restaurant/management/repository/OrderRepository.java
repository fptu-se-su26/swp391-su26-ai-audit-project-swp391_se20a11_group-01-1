package com.restaurant.management.repository;

import com.restaurant.management.entity.order.OrderStatus;
import com.restaurant.management.entity.order.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<RestaurantOrder, Long> {
    List<RestaurantOrder> findByCustomerId(Long customerId);
    Optional<RestaurantOrder> findByIdAndCustomerId(Long id, Long customerId);
    List<RestaurantOrder> findByOrderStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM RestaurantOrder o WHERE o.orderStatus = :status")
    Long countByOrderStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM RestaurantOrder o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    Long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o.orderStatus, COUNT(o) FROM RestaurantOrder o WHERE o.createdAt >= :start AND o.createdAt <= :end GROUP BY o.orderStatus")
    List<Object[]> countGroupedByStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM RestaurantOrder o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    java.math.BigDecimal sumTotalAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM RestaurantOrder o WHERE o.createdAt >= :start AND o.createdAt <= :end AND o.orderStatus = :status")
    Long countByDateRangeAndStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM RestaurantOrder o WHERE o.createdAt >= :start AND o.createdAt <= :end AND o.orderStatus = :status")
    java.math.BigDecimal sumTotalAmountByDateRangeAndStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("status") OrderStatus status);
}
