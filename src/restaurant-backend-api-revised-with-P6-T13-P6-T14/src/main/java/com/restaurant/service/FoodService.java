package com.restaurant.service;
import com.restaurant.dto.food.*; import com.restaurant.entity.*; import com.restaurant.exception.*; import com.restaurant.model.*; import com.restaurant.repository.*; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service @RequiredArgsConstructor
public class FoodService {
 private final FoodRepository foodRepository; private final CategoryRepository categoryRepository;
 public List<CategoryResponse> categories(){ return categoryRepository.findAll().stream().map(this::toCategory).toList(); }
 public List<FoodResponse> listAvailable(){ return foodRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE).stream().map(this::toFood).toList(); }
 public List<FoodResponse> listAll(){ return foodRepository.findAll().stream().map(this::toFood).toList(); }
 public FoodResponse detail(Long id){ return toFood(getFood(id)); }
 public List<FoodResponse> byCategory(Long categoryId){ return foodRepository.findByCategory_CategoryId(categoryId).stream().map(this::toFood).toList(); }
 public List<FoodResponse> search(String keyword){ if(keyword==null || keyword.isBlank()) return listAvailable(); return foodRepository.findByFoodNameContainingIgnoreCase(keyword).stream().map(this::toFood).toList(); }
 @Transactional public FoodResponse create(FoodRequest r){ Category c=categoryRepository.findById(r.getCategoryId()).orElseThrow(()->new ResourceNotFoundException("Category not found")); Food f=Food.builder().category(c).foodName(r.getFoodName()).description(r.getDescription()).price(r.getPrice()).availabilityStatus(parseStatus(r.getAvailabilityStatus())).estimatedCookingTime(r.getEstimatedCookingTime()).build(); attachImages(f,r.getImageUrls()); return toFood(foodRepository.save(f)); }
 @Transactional public FoodResponse update(Long id, FoodRequest r){ Food f=getFood(id); Category c=categoryRepository.findById(r.getCategoryId()).orElseThrow(()->new ResourceNotFoundException("Category not found")); f.setCategory(c); f.setFoodName(r.getFoodName()); f.setDescription(r.getDescription()); f.setPrice(r.getPrice()); f.setAvailabilityStatus(parseStatus(r.getAvailabilityStatus())); f.setEstimatedCookingTime(r.getEstimatedCookingTime()); f.getImages().clear(); attachImages(f,r.getImageUrls()); return toFood(foodRepository.save(f)); }
 @Transactional public FoodResponse updateAvailability(Long id, String status){ Food f=getFood(id); f.setAvailabilityStatus(parseStatus(status)); return toFood(foodRepository.save(f)); }
 @Transactional public void delete(Long id){ Food f=getFood(id); f.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE); foodRepository.save(f); }
 private Food getFood(Long id){ return foodRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Food not found")); }
 private AvailabilityStatus parseStatus(String s){ if(s==null||s.isBlank()) return AvailabilityStatus.AVAILABLE; try{return AvailabilityStatus.valueOf(s.toUpperCase());}catch(Exception e){throw new BadRequestException("Invalid food status");} }
 private void attachImages(Food f,List<String> urls){ if(urls!=null) for(int i=0;i<urls.size();i++){ f.getImages().add(FoodImage.builder().food(f).imageUrl(urls.get(i)).isPrimary(i==0).build()); }}
 private FoodResponse toFood(Food f){ return FoodResponse.builder().foodId(f.getFoodId()).categoryId(f.getCategory().getCategoryId()).categoryName(f.getCategory().getCategoryName()).foodName(f.getFoodName()).description(f.getDescription()).price(f.getPrice()).availabilityStatus(String.valueOf(f.getAvailabilityStatus())).estimatedCookingTime(f.getEstimatedCookingTime()).imageUrls(f.getImages().stream().map(FoodImage::getImageUrl).toList()).build(); }
 private CategoryResponse toCategory(Category c){ return CategoryResponse.builder().categoryId(c.getCategoryId()).categoryName(c.getCategoryName()).description(c.getDescription()).status(String.valueOf(c.getStatus())).build(); }
}
