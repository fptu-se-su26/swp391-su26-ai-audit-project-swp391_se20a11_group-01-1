package com.restaurant.dto.staff;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateTableStatusRequest {
    @NotBlank(message = "tableStatus is required")
    private String tableStatus; // AVAILABLE, RESERVED, OCCUPIED, OUT_OF_SERVICE
}
