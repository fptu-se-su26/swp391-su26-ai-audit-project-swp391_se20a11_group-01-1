package com.restaurant.management.service;

import com.restaurant.management.dto.menu.FoodDetailResponse;
import com.restaurant.management.dto.menu.FoodRequest;
import com.restaurant.management.dto.menu.FoodResponse;
import com.restaurant.management.dto.menu.UpdateFoodStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FoodService {
    Page<FoodResponse> getPublicFoods(String keyword, Long categoryId, Boolean isAvailable, Pageable pageable);
    Page<FoodResponse> getAdminFoods(String keyword, Long categoryId, Pageable pageable);
    FoodDetailResponse getFoodDetail(Long id);
    FoodResponse createFood(FoodRequest request);
    FoodResponse updateFood(Long id, FoodRequest request);
    void updateFoodStatus(Long id, UpdateFoodStatusRequest request);
}
