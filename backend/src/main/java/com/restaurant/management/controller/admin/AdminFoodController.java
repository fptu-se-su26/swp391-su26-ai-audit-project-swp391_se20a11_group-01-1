package com.restaurant.management.controller.admin;

import com.restaurant.management.dto.menu.FoodRequest;
import com.restaurant.management.dto.menu.FoodResponse;
import com.restaurant.management.dto.menu.UpdateFoodStatusRequest;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/foods")
@RequiredArgsConstructor
public class AdminFoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FoodResponse>>> getAdminFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FoodResponse> foods = foodService.getAdminFoods(keyword, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(foods, "Foods fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodResponse>> createFood(@Valid @RequestBody FoodRequest request) {
        FoodResponse food = foodService.createFood(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(food, "Food created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResponse>> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody FoodRequest request) {
        FoodResponse food = foodService.updateFood(id, request);
        return ResponseEntity.ok(ApiResponse.success(food, "Food updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateFoodStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFoodStatusRequest request) {
        foodService.updateFoodStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Food status updated successfully"));
    }
}
