package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.FoodRequest;
import com.rms.restaurant_management_system.dto.response.FoodResponse;

import java.util.List;

public interface FoodService {
    List<FoodResponse> getAllFoods();
    FoodResponse getFoodById(Long id);
    FoodResponse createFood(FoodRequest request);
    FoodResponse updateFood(Long id, FoodRequest request);
    void deleteFood(Long id);
}
