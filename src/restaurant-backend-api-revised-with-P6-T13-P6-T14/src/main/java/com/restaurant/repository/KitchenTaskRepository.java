package com.restaurant.repository;

import com.restaurant.entity.KitchenTask;
import com.restaurant.model.KitchenTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface KitchenTaskRepository extends JpaRepository<KitchenTask, Long> {
    Optional<KitchenTask> findTopByOrderItem_OrderItemIdOrderByKitchenTaskIdDesc(Long orderItemId);
    List<KitchenTask> findByTaskStatusOrderByKitchenTaskIdDesc(KitchenTaskStatus taskStatus);
}
