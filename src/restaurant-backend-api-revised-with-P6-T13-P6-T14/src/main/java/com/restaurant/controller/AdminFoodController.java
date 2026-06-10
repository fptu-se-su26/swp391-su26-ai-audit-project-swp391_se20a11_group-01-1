package com.restaurant.controller;
import com.restaurant.common.ApiResponse; import com.restaurant.dto.food.*; import com.restaurant.service.FoodService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.util.List; import java.util.Map;
@RestController @RequestMapping("/admin/foods") @RequiredArgsConstructor
public class AdminFoodController { private final FoodService foodService;
 @GetMapping public ApiResponse<List<FoodResponse>> all(){ return ApiResponse.ok("All foods", foodService.listAll()); }
 @PostMapping public ApiResponse<FoodResponse> create(@Valid @RequestBody FoodRequest request){ return ApiResponse.ok("Food created", foodService.create(request)); }
 @PutMapping("/{id}") public ApiResponse<FoodResponse> update(@PathVariable Long id,@Valid @RequestBody FoodRequest request){ return ApiResponse.ok("Food updated", foodService.update(id, request)); }
 @PatchMapping("/{id}/availability") public ApiResponse<FoodResponse> availability(@PathVariable Long id,@RequestBody Map<String,String> body){ return ApiResponse.ok("Food availability updated", foodService.updateAvailability(id, body.get("availabilityStatus"))); }
 @DeleteMapping("/{id}") public ApiResponse<Void> delete(@PathVariable Long id){ foodService.delete(id); return ApiResponse.ok("Food disabled", null); }
}
