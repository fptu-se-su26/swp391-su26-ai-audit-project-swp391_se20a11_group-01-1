package com.rms.restaurant_management_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class VoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotNull(message = "Mức giảm không được để trống")
    @Min(value = 0, message = "Mức giảm phải lớn hơn 0")
    private BigDecimal discount;

    @NotBlank(message = "Loại voucher không được để trống")
    private String type;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @Min(value = 0, message = "Giá trị đơn tối thiểu không được âm")
    private BigDecimal minOrder;

    @NotNull(message = "Tổng số lượng không được để trống")
    @Min(value = 1, message = "Tổng số lượng phải ít nhất là 1")
    private Integer total;

    private Boolean active;

    @NotNull(message = "Hạn sử dụng không được để trống")
    private LocalDate expiry;
}
