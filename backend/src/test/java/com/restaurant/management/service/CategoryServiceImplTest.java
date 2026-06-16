package com.restaurant.management.service;

import com.restaurant.management.dto.menu.CategoryRequest;
import com.restaurant.management.dto.menu.CategoryResponse;
import com.restaurant.management.entity.menu.FoodCategory;
import com.restaurant.management.exception.ConflictException;
import com.restaurant.management.repository.FoodCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private FoodCategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private FoodCategory category;

    @BeforeEach
    void setUp() {
        category = FoodCategory.builder()
                .id(1L)
                .name("Main Course")
                .build();
    }

    @Test
    void testGetActiveCategories_Success() {
        when(categoryRepository.findByDeletedAtIsNull()).thenReturn(Collections.singletonList(category));

        List<CategoryResponse> result = categoryService.getActiveCategories();

        assertEquals(1, result.size());
        assertEquals("Main Course", result.get(0).getName());
    }

    @Test
    void testCreateCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Dessert");

        FoodCategory newCategory = FoodCategory.builder().id(2L).name("Dessert").build();

        when(categoryRepository.existsByName("Dessert")).thenReturn(false);
        when(categoryRepository.save(any(FoodCategory.class))).thenReturn(newCategory);

        CategoryResponse result = categoryService.createCategory(request);

        assertEquals("Dessert", result.getName());
    }

    @Test
    void testCreateCategory_DuplicateName_ThrowsException() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Main Course");

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.createCategory(request));
    }
}
