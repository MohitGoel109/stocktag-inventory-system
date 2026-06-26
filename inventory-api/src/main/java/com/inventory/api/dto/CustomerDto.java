package com.inventory.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerDto(
        Long id,
        @NotBlank @Size(max = 200) String name,
        String mobileNumber,
        @Email String email
) {}
