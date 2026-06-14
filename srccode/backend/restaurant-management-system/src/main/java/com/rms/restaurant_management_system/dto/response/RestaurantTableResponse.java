package com.rms.restaurant_management_system.dto.response;

import com.rms.restaurant_management_system.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTableResponse {
    private Long id;
    private Integer number;
    private Integer capacity;
    private TableStatus status;
}
