package com.restaurant.management.service;

import com.restaurant.management.dto.menu.FoodDetailResponse;
import com.restaurant.management.dto.menu.FoodRequest;
import com.restaurant.management.dto.menu.FoodResponse;
import com.restaurant.management.entity.menu.FoodCategory;
import com.restaurant.management.entity.menu.FoodItem;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.FoodCategoryRepository;
import com.restaurant.management.repository.FoodItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceImplTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private FoodCategoryRepository foodCategoryRepository;

    @InjectMocks
    private FoodServiceImpl foodService;

    private FoodCategory category;
    private FoodItem foodItem;

    @BeforeEach
    void setUp() {
        category = FoodCategory.builder().id(1L).name("Pizza").build();

        foodItem = FoodItem.builder()
                .id(1L)
                .category(category)
                .name("Margherita")
                .price(new BigDecimal("10.99"))
                .isAvailable(true)
                .build();
    }

    @Test
    void testGetPublicFoods_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FoodItem> page = new PageImpl<>(Collections.singletonList(foodItem));

        when(foodItemRepository.searchActiveFoods(null, null, true, pageable)).thenReturn(page);

        Page<FoodResponse> result = foodService.getPublicFoods(null, null, true, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Margherita", result.getContent().get(0).getName());
    }

    @Test
    void testGetFoodDetail_Success() {
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));

        FoodDetailResponse result = foodService.getFoodDetail(1L);

        assertEquals("Margherita", result.getName());
        assertEquals("Pizza", result.getCategory().getName());
    }

    @Test
    void testGetFoodDetail_NotFound_ThrowsException() {
        when(foodItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> foodService.getFoodDetail(99L));
    }

    @Test
    void testCreateFood_Success() {
        FoodRequest request = new FoodRequest();
        request.setCategoryId(1L);
        request.setName("Pepperoni");
        request.setPrice(new BigDecimal("12.99"));

        FoodItem newFood = FoodItem.builder()
                .id(2L)
                .category(category)
                .name("Pepperoni")
                .price(new BigDecimal("12.99"))
                .isAvailable(true)
                .build();

        when(foodCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(foodItemRepository.save(any(FoodItem.class))).thenReturn(newFood);

        FoodResponse result = foodService.createFood(request);

        assertEquals("Pepperoni", result.getName());
        assertEquals(new BigDecimal("12.99"), result.getPrice());
    }
}
