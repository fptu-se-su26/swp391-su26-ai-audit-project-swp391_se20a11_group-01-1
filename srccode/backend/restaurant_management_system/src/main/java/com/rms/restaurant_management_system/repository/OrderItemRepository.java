package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @EntityGraph(attributePaths = "order")
    List<OrderItem> findByStatusInOrderByCreatedAtAsc(Collection<OrderItemStatus> statuses);

    @Query("select item.order.orderId from OrderItem item where item.orderItemId = :orderItemId")
    Optional<Long> findOrderIdByOrderItemId(@Param("orderItemId") Long orderItemId);
}
