package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.FeedbackStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackResponse {

    private Long feedbackId;

    private Long userId;

    private String username;

    private String customerName;

    private String customerEmail;

    private Integer rating;

    private String content;

    private FeedbackStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}