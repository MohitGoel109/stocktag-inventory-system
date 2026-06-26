package com.inventory.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        Long id,
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Email String email,
        // Only required when creating a new user; ignored on update unless resetting.
        String password,
        String role,
        String mobileNumber,
        String address,
        String status
) {}
