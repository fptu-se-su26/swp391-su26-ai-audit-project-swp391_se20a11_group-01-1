package com.rms.restaurant_management_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateVoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotNull(message = "Tổng tiền đơn hàng không được để trống")
    @Min(value = 0, message = "Tổng tiền đơn hàng không được âm")
    private BigDecimal orderTotal;
}
