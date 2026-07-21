package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.FeedbackRequest;
import com.rms.restaurant_management_system.dto.request.UpdateFeedbackStatusRequest;
import com.rms.restaurant_management_system.dto.response.FeedbackResponse;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.service.interfaces.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public FeedbackResponse createFeedback(@Valid @RequestBody FeedbackRequest request,
                                           @AuthenticationPrincipal User actor) {
        request.setUserId(actor.getUserId());
        return feedbackService.createFeedback(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }

    @GetMapping("/customer/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeedbackResponse> getFeedbacksByCustomer(@PathVariable Long userId) {
        return feedbackService.getFeedbacksByCustomer(userId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<FeedbackResponse> getMyFeedbacks(@AuthenticationPrincipal User actor) {
        return feedbackService.getFeedbacksByCustomer(actor.getUserId());
    }
    @PutMapping("/{feedbackId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public FeedbackResponse updateFeedbackStatus(
            @PathVariable Long feedbackId,
            @RequestBody UpdateFeedbackStatusRequest request
    ) {
        return feedbackService.updateFeedbackStatus(feedbackId, request);
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("@domainAuthorization.canDeleteFeedback(#feedbackId, authentication)")
    public void deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
    }
}
