package com.inventory.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String orderCode,
        @NotNull Long customerId,
        String customerName,
        LocalDateTime orderDate,
        BigDecimal totalPaid,
        @NotEmpty @Valid List<OrderItemDto> items
) {}
