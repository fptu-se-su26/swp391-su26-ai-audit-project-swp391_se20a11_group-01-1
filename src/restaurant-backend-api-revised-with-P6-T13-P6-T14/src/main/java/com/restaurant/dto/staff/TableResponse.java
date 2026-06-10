package com.restaurant.dto.staff;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TableResponse {
    private Long tableId;
    private String tableNumber;
    private Integer capacity;
    private String tableStatus;
    private String location;
}
