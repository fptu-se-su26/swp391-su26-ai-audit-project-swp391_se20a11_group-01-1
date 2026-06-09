package com.restaurant.repository;

import com.restaurant.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {

    Optional<FoodCategory> findByCategoryName(String categoryName);

    boolean existsByCategoryName(String categoryName);

    List<FoodCategory> findByStatus(String status);
}
