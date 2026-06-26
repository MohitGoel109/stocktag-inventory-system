package com.inventory.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemDto(
        Long productId,
        String productName,
        @NotNull @Min(1) Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
