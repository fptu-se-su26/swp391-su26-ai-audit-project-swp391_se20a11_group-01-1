package com.restaurant.repository;

import com.restaurant.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_Id(Long orderId);

    List<OrderItem> findByFoodItem_Id(Long foodItemId);

    List<OrderItem> findByItemStatus(String itemStatus);

    List<OrderItem> findByOrder_IdAndItemStatus(Long orderId, String itemStatus);
}
