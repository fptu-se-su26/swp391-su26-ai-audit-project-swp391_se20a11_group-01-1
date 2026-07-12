package com.rms.restaurant_management_system.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableActionRequest {

    private Long targetTableId;
    private String status;
}
