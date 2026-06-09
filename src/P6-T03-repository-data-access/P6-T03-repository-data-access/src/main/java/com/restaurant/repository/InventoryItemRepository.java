package com.restaurant.repository;

import com.restaurant.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByStatus(String status);

    List<InventoryItem> findByItemNameContainingIgnoreCase(String keyword);
}
