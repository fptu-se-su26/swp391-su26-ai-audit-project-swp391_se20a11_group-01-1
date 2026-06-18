package com.restaurant.management.dto.reservation;

import com.restaurant.management.entity.reservation.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private Long id;
    private String tableNumber;
    private Integer capacity;
    private TableStatus status;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
