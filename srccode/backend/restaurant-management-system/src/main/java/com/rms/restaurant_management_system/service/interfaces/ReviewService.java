package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.ReviewRequest;
import com.rms.restaurant_management_system.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, String email);
    List<ReviewResponse> getAllReviews();
}
