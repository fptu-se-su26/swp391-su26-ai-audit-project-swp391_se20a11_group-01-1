package com.restaurant.controller;
import com.restaurant.common.ApiResponse; import com.restaurant.dto.food.*; import com.restaurant.service.FoodService; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequiredArgsConstructor
public class FoodController { private final FoodService foodService;
 @GetMapping("/categories") public ApiResponse<List<CategoryResponse>> categories(){ return ApiResponse.ok("Categories", foodService.categories()); }
 @GetMapping("/foods") public ApiResponse<List<FoodResponse>> foods(@RequestParam(required=false) String keyword){ return ApiResponse.ok("Foods", keyword==null?foodService.listAvailable():foodService.search(keyword)); }
 @GetMapping("/foods/{id}") public ApiResponse<FoodResponse> detail(@PathVariable Long id){ return ApiResponse.ok("Food detail", foodService.detail(id)); }
 @GetMapping("/foods/categories/{categoryId}") public ApiResponse<List<FoodResponse>> byCategory(@PathVariable Long categoryId){ return ApiResponse.ok("Foods by category", foodService.byCategory(categoryId)); }
}
