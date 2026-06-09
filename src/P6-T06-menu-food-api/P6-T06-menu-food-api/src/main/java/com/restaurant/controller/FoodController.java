package com.restaurant.controller;

import com.restaurant.dto.food.FoodResponse;
import com.restaurant.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getFoods(
            @RequestParam(name = "availableOnly", defaultValue = "false") boolean availableOnly
    ) {
        if (availableOnly) {
            return ResponseEntity.ok(foodService.getAvailableFoods());
        }
        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodDetail(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<FoodResponse>> getFoodsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(foodService.getFoodsByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodResponse>> searchFoods(@RequestParam String keyword) {
        return ResponseEntity.ok(foodService.searchFoods(keyword));
    }
}
