package com.restaurant.management.controller.menu;

import com.restaurant.management.dto.menu.FoodDetailResponse;
import com.restaurant.management.dto.menu.FoodResponse;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FoodResponse>>> getPublicFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FoodResponse> foods = foodService.getPublicFoods(keyword, categoryId, isAvailable, pageable);
        return ResponseEntity.ok(ApiResponse.success(foods, "Foods fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodDetailResponse>> getFoodDetail(@PathVariable Long id) {
        FoodDetailResponse foodDetail = foodService.getFoodDetail(id);
        return ResponseEntity.ok(ApiResponse.success(foodDetail, "Food details fetched successfully"));
    }
}
