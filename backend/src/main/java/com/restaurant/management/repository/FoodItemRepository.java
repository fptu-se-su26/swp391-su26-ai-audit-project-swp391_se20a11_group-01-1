package com.restaurant.management.repository;

import com.restaurant.management.entity.menu.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    @Query("SELECT f FROM FoodItem f WHERE f.deletedAt IS NULL AND " +
           "(:keyword IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR f.category.id = :categoryId) AND " +
           "(:isAvailable IS NULL OR f.isAvailable = :isAvailable)")
    Page<FoodItem> searchActiveFoods(@Param("keyword") String keyword, 
                                     @Param("categoryId") Long categoryId, 
                                     @Param("isAvailable") Boolean isAvailable, 
                                     Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE f.deletedAt IS NULL AND " +
           "(:keyword IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR f.category.id = :categoryId)")
    Page<FoodItem> searchAllFoods(@Param("keyword") String keyword, 
                                  @Param("categoryId") Long categoryId, 
                                  Pageable pageable);
}
