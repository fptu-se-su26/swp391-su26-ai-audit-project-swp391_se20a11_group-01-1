package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.FoodRequest;
import com.rms.restaurant_management_system.dto.response.FoodResponse;
import com.rms.restaurant_management_system.entity.Category;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.CategoryRepository;
import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.service.interfaces.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FoodResponse getFoodById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public FoodResponse createFood(FoodRequest request) {
        Category category = findCategory(request.getCategoryId());
        Food food = Food.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();
        return toResponse(foodRepository.save(food));
    }

    @Override
    public FoodResponse updateFood(Long id, FoodRequest request) {
        Food food = findById(id);
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        food.setCategory(findCategory(request.getCategoryId()));
        return toResponse(foodRepository.save(food));
    }

    @Override
    public void deleteFood(Long id) {
        foodRepository.delete(findById(id));
    }

    private Food findById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private FoodResponse toResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .status(food.getStatus())
                .categoryId(food.getCategory() != null ? food.getCategory().getId() : null)
                .categoryName(food.getCategory() != null ? food.getCategory().getName() : null)
                .createdAt(food.getCreatedAt())
                .build();
    }
}
