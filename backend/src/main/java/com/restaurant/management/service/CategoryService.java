package com.restaurant.management.service;

import com.restaurant.management.dto.menu.CategoryRequest;
import com.restaurant.management.dto.menu.CategoryResponse;
import com.restaurant.management.dto.menu.UpdateCategoryStatusRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getActiveCategories();
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void updateCategoryStatus(Long id, UpdateCategoryStatusRequest request);
}
