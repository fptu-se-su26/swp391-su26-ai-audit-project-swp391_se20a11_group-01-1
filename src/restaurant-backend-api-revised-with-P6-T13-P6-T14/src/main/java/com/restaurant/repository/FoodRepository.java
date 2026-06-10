package com.restaurant.repository;
import com.restaurant.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByAvailabilityStatus(com.restaurant.model.AvailabilityStatus status);
    List<Food> findByCategory_CategoryId(Long categoryId);
    List<Food> findByFoodNameContainingIgnoreCase(String keyword);
}
