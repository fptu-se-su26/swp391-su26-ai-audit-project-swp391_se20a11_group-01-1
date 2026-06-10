package com.restaurant.dto.order; import jakarta.validation.constraints.NotBlank; import lombok.Data; @Data public class UpdateOrderStatusRequest { @NotBlank private String orderStatus; }
