package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.FoodRequest;
import com.rms.restaurant_management_system.dto.response.FoodResponse;
import com.rms.restaurant_management_system.entity.Category;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.repository.CategoryRepository;
import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.service.interfaces.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> getAvailableFoods() {
        return foodRepository.findByIsAvailableTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> getFoodsByCategory(Long categoryId) {
        return foodRepository.findByCategoryCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public FoodResponse getFoodById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với id: " + id));

        return mapToResponse(food);
    }

    @Override
    public FoodResponse createFood(FoodRequest request) {
        validateFoodRequest(request);

        if (foodRepository.existsByFoodName(request.getFoodName())) {
            throw new RuntimeException("Tên món ăn đã tồn tại");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category với id: " + request.getCategoryId()));

        Food food = Food.builder()
                .foodName(request.getFoodName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .emoji(request.getEmoji())
                .rating(request.getRating() != null ? request.getRating() : 0.0)
                .orders(request.getOrders() != null ? request.getOrders() : 0)
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .category(category)
                .build();

        Food savedFood = foodRepository.save(food);

        return mapToResponse(savedFood);
    }

    @Override
    public FoodResponse updateFood(Long id, FoodRequest request) {
        validateFoodRequest(request);

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category với id: " + request.getCategoryId()));

        food.setFoodName(request.getFoodName().trim());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        food.setEmoji(request.getEmoji());

        if (request.getRating() != null) {
            food.setRating(request.getRating());
        }

        if (request.getOrders() != null) {
            food.setOrders(request.getOrders());
        }

        if (request.getIsAvailable() != null) {
            food.setIsAvailable(request.getIsAvailable());
        }

        food.setCategory(category);

        Food updatedFood = foodRepository.save(food);

        return mapToResponse(updatedFood);
    }

    @Override
    public FoodResponse toggleAvailable(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với id: " + id));

        food.setIsAvailable(!food.getIsAvailable());

        Food updatedFood = foodRepository.save(food);

        return mapToResponse(updatedFood);
    }

    @Override
    public void deleteFood(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với id: " + id));

        foodRepository.delete(food);
    }

    private FoodResponse mapToResponse(Food food) {
        return FoodResponse.builder()
                .foodId(food.getFoodId())
                .foodName(food.getFoodName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .emoji(food.getEmoji())
                .rating(food.getRating())
                .orders(food.getOrders())
                .isAvailable(food.getIsAvailable())
                .categoryId(food.getCategory().getCategoryId())
                .categoryName(food.getCategory().getCategoryName())
                .createdAt(food.getCreatedAt())
                .updatedAt(food.getUpdatedAt())
                .build();
    }

    private void validateFoodRequest(FoodRequest request) {
        if (request.getFoodName() == null || request.getFoodName().trim().isEmpty()) {
            throw new RuntimeException("Tên món ăn không được để trống");
        }

        if (request.getPrice() == null) {
            throw new RuntimeException("Giá món ăn không được để trống");
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá món ăn không được âm");
        }

        if (request.getCategoryId() == null) {
            throw new RuntimeException("Category không được để trống");
        }
    }
}