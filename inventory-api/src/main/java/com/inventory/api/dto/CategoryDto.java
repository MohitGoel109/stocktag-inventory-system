package com.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        Long id,
        @NotBlank @Size(max = 200) String name,
        Long productCount
) {}
