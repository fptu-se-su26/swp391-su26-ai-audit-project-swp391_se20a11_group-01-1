package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rms.restaurant_management_system.enums.OrderItemStatus;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByStatusIn(List<OrderItemStatus> statuses);
}