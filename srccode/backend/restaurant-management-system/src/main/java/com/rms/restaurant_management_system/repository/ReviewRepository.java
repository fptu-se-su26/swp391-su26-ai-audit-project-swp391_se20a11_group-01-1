package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFoodId(Long foodId);
    List<Review> findByUserId(Long userId);
}
