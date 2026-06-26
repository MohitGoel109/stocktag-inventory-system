package com.inventory.api.dto;

public record LoginResponse(
        String token,
        String name,
        String email,
        String role,
        Long userId
) {}
