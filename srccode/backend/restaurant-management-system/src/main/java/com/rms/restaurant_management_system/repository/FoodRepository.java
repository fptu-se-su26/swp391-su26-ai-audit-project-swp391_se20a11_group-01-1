package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.enums.FoodStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByCategoryId(Long categoryId);
    List<Food> findByStatus(FoodStatus status);
}
