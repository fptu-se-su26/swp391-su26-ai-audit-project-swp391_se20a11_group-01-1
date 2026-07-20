package com.rms.restaurant_management_system.dto.request;

import com.rms.restaurant_management_system.enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Mức giảm không được để trống")
    @DecimalMin(value = "0.01", message = "Mức giảm phải lớn hơn 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.01", message = "Mức giảm tối đa phải lớn hơn 0")
    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @Min(value = 0, message = "Giá trị đơn tối thiểu không được âm")
    private BigDecimal minOrderAmount;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startAt;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endAt;

    @NotNull(message = "Giới hạn sử dụng không được để trống")
    @Min(value = 1, message = "Giới hạn sử dụng phải ít nhất là 1")
    private Integer usageLimit;

    @NotNull(message = "Giới hạn sử dụng mỗi người không được để trống")
    @Min(value = 1, message = "Giới hạn sử dụng mỗi người phải ít nhất là 1")
    private Integer usageLimitPerUser;

    private Boolean active;
}
