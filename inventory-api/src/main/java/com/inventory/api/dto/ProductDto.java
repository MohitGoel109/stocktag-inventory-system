package com.inventory.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        @NotBlank @Size(max = 200) String name,
        @NotNull @Min(0) Integer quantity,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal price,
        @Size(max = 500) String description,
        @NotNull Long categoryId,
        String categoryName,
        Boolean lowStock
) {}
