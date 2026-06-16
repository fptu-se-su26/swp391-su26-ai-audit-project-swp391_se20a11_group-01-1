package com.restaurant.management.service;

import com.restaurant.management.dto.menu.CategoryResponse;
import com.restaurant.management.dto.menu.FoodDetailResponse;
import com.restaurant.management.dto.menu.FoodRequest;
import com.restaurant.management.dto.menu.FoodResponse;
import com.restaurant.management.dto.menu.UpdateFoodStatusRequest;
import com.restaurant.management.entity.menu.FoodCategory;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.FoodCategoryRepository;
import com.restaurant.management.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodItemRepository foodItemRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<FoodResponse> getPublicFoods(String keyword, Long categoryId, Boolean isAvailable, Pageable pageable) {
        return foodItemRepository.searchActiveFoods(keyword, categoryId, isAvailable, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodResponse> getAdminFoods(String keyword, Long categoryId, Pageable pageable) {
        return foodItemRepository.searchAllFoods(keyword, categoryId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodDetailResponse getFoodDetail(Long id) {
        FoodItem food = foodItemRepository.findById(id)
                .filter(f -> f.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        return FoodDetailResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .isAvailable(food.getIsAvailable())
                .category(CategoryResponse.builder()
                        .id(food.getCategory().getId())
                        .name(food.getCategory().getName())
                        .description(food.getCategory().getDescription())
                        .imageUrl(food.getCategory().getImageUrl())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public FoodResponse createFood(FoodRequest request) {
        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        FoodItem foodItem = FoodItem.builder()
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        FoodItem savedFood = foodItemRepository.save(foodItem);
        return mapToResponse(savedFood);
    }

    @Override
    @Transactional
    public FoodResponse updateFood(Long id, FoodRequest request) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .filter(f -> f.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        foodItem.setCategory(category);
        foodItem.setName(request.getName());
        foodItem.setDescription(request.getDescription());
        foodItem.setPrice(request.getPrice());
        foodItem.setImageUrl(request.getImageUrl());
        
        if (request.getIsAvailable() != null) {
            foodItem.setIsAvailable(request.getIsAvailable());
        }

        FoodItem updatedFood = foodItemRepository.save(foodItem);
        return mapToResponse(updatedFood);
    }

    @Override
    @Transactional
    public void updateFoodStatus(Long id, UpdateFoodStatusRequest request) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .filter(f -> f.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));

        foodItem.setIsAvailable(request.getIsAvailable());
        foodItemRepository.save(foodItem);
    }

    private FoodResponse mapToResponse(FoodItem foodItem) {
        return FoodResponse.builder()
                .id(foodItem.getId())
                .name(foodItem.getName())
                .price(foodItem.getPrice())
                .imageUrl(foodItem.getImageUrl())
                .isAvailable(foodItem.getIsAvailable())
                .categoryId(foodItem.getCategory().getId())
                .categoryName(foodItem.getCategory().getName())
                .build();
    }
}
