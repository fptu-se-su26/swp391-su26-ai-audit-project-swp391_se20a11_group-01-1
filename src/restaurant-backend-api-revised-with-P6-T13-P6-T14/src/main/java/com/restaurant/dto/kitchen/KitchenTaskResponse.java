package com.restaurant.dto.kitchen;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class KitchenTaskResponse {
    private Long kitchenTaskId;
    private Long orderItemId;
    private Long orderId;
    private String foodName;
    private String taskStatus;
    private String itemStatus;
    private String kitchenUserName;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String note;
}
