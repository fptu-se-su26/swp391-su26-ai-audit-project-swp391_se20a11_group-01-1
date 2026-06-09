package com.restaurant.service;

import com.restaurant.dto.food.FoodRequest;
import com.restaurant.dto.food.FoodResponse;
import com.restaurant.entity.FoodCategory;
import com.restaurant.entity.FoodItem;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.FoodCategoryRepository;
import com.restaurant.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodService {

    private final FoodItemRepository foodItemRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    @Transactional(readOnly = true)
    public List<FoodResponse> getAllFoods() {
        return foodItemRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> getAvailableFoods() {
        return foodItemRepository.findByAvailableTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FoodResponse getFoodById(Long id) {
        FoodItem food = findFoodOrThrow(id);
        return toResponse(food);
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> getFoodsByCategory(Long categoryId) {
        return foodItemRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> searchFoods(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllFoods();
        }
        return foodItemRepository.findByNameContainingIgnoreCase(keyword.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FoodResponse createFood(FoodRequest request) {
        FoodCategory category = findCategoryOrThrow(request.getCategoryId());

        FoodItem food = new FoodItem();
        applyRequestToFood(food, request, category);

        FoodItem savedFood = foodItemRepository.save(food);
        return toResponse(savedFood);
    }

    public FoodResponse updateFood(Long id, FoodRequest request) {
        FoodItem food = findFoodOrThrow(id);
        FoodCategory category = findCategoryOrThrow(request.getCategoryId());

        applyRequestToFood(food, request, category);

        FoodItem updatedFood = foodItemRepository.save(food);
        return toResponse(updatedFood);
    }

    public void deleteFood(Long id) {
        FoodItem food = findFoodOrThrow(id);

        // Demo-safe delete: không xóa cứng để tránh lỗi FK với order_items.
        food.setAvailable(false);
        foodItemRepository.save(food);
    }

    public FoodResponse updateAvailability(Long id, Boolean available) {
        FoodItem food = findFoodOrThrow(id);
        food.setAvailable(available);
        return toResponse(foodItemRepository.save(food));
    }

    private FoodItem findFoodOrThrow(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
    }

    private FoodCategory findCategoryOrThrow(Long id) {
        return foodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food category not found with id: " + id));
    }

    private void applyRequestToFood(FoodItem food, FoodRequest request, FoodCategory category) {
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        food.setAvailable(request.getAvailable() == null || request.getAvailable());
        food.setCategory(category);
    }

    private FoodResponse toResponse(FoodItem food) {
        FoodCategory category = food.getCategory();

        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .available(food.getAvailable())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .build();
    }
}
