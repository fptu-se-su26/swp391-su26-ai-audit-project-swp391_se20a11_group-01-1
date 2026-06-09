package com.restaurant.repository;

import com.restaurant.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByAvailableTrue();

    List<FoodItem> findByStatus(String status);

    List<FoodItem> findByCategory_Id(Long categoryId);

    List<FoodItem> findByFoodNameContainingIgnoreCase(String keyword);

    List<FoodItem> findByAvailableTrueAndStatus(String status);
}
