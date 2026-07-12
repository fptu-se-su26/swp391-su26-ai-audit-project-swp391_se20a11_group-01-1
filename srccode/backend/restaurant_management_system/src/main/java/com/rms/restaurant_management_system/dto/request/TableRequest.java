package com.rms.restaurant_management_system.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {

    private String tableName;
    private Integer capacity;
    private String status;
    private String reservedBy;
}
