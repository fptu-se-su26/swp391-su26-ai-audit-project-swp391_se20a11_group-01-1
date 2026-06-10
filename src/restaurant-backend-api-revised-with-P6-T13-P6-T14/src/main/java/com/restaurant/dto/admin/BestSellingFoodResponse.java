package com.restaurant.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class BestSellingFoodResponse {
    private Long foodId;
    private String foodName;
    private Long soldQuantity;
    private BigDecimal revenue;
}
