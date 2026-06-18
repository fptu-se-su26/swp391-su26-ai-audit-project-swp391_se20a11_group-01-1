package com.restaurant.management.dto.reservation;

import com.restaurant.management.entity.reservation.TableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTableStatusRequest {
    @NotNull(message = "Status is required")
    private TableStatus status;
}
