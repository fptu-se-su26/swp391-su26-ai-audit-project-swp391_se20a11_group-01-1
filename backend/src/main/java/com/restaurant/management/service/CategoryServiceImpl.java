package com.restaurant.management.service;

import com.restaurant.management.dto.menu.CategoryRequest;
import com.restaurant.management.dto.menu.CategoryResponse;
import com.restaurant.management.dto.menu.UpdateCategoryStatusRequest;
import com.restaurant.management.entity.menu.FoodCategory;
import com.restaurant.management.exception.ConflictException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.FoodCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final FoodCategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Category name already exists");
        }

        FoodCategory category = FoodCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        FoodCategory savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        FoodCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Category name already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        FoodCategory updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void updateCategoryStatus(Long id, UpdateCategoryStatusRequest request) {
        FoodCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (request.getIsActive()) {
            category.setDeletedAt(null);
            category.setDeletedBy(null);
        } else {
            if (category.getDeletedAt() == null) {
                category.setDeletedAt(LocalDateTime.now());
                // In a real application we would set deletedBy from SecurityContext
            }
        }
        categoryRepository.save(category);
    }

    private CategoryResponse mapToResponse(FoodCategory category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .build();
    }
}
