package com.rms.restaurant_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    private Long id;
    private String code;
    private BigDecimal discount;
    private String type;
    private BigDecimal minOrder;
    private Integer used;
    private Integer total;
    private Boolean active;
    private LocalDate expiry;
}
