package com.restaurant.dto.cart; import jakarta.validation.constraints.*; import lombok.Data; @Data public class UpdateCartItemRequest { @NotNull @Min(1) private Integer quantity; }
