package com.inventory.api.service;

import com.inventory.api.dto.UserDto;
import com.inventory.api.entity.AppUser;
import com.inventory.api.exception.BadRequestException;
import com.inventory.api.exception.ConflictException;
import com.inventory.api.exception.NotFoundException;
import com.inventory.api.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        return appUserRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public UserDto create(UserDto dto) {
        if (appUserRepository.existsByEmail(dto.email())) {
            throw new ConflictException("A user with email '" + dto.email() + "' already exists");
        }
        if (dto.password() == null || dto.password().isBlank()) {
            throw new BadRequestException("Password is required when creating a user");
        }
        validatePasswordStrength(dto.password());

        AppUser user = AppUser.builder()
                .name(dto.name().trim())
                .email(dto.email().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .role(parseRole(dto.role()))
                .mobileNumber(dto.mobileNumber())
                .address(dto.address())
                .status(parseStatus(dto.status(), AppUser.UserStatus.ACTIVE))
                .build();

        return toDto(appUserRepository.save(user));
    }

    @Transactional
    public UserDto update(Long id, UserDto dto) {
        AppUser user = getEntity(id);

        user.setName(dto.name().trim());
        user.setMobileNumber(dto.mobileNumber());
        user.setAddress(dto.address());
        if (dto.status() != null) {
            user.setStatus(parseStatus(dto.status(), user.getStatus()));
        }
        if (dto.role() != null) {
            user.setRole(parseRole(dto.role()));
        }
        // Password is only changed if a new one was explicitly provided.
        if (dto.password() != null && !dto.password().isBlank()) {
            validatePasswordStrength(dto.password());
            user.setPasswordHash(passwordEncoder.encode(dto.password()));
        }

        return toDto(appUserRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        AppUser user = getEntity(id);
        appUserRepository.delete(user);
    }

    private void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
    }

    private AppUser.Role parseRole(String role) {
        try {
            return AppUser.Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Role must be ADMIN or STAFF");
        }
    }

    private AppUser.UserStatus parseStatus(String status, AppUser.UserStatus fallback) {
        if (status == null || status.isBlank()) return fallback;
        try {
            return AppUser.UserStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Status must be ACTIVE or INACTIVE");
        }
    }

    private AppUser getEntity(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

    private UserDto toDto(AppUser u) {
        // Never return the password hash to the client.
        return new UserDto(u.getId(), u.getName(), u.getEmail(), null,
                u.getRole().name(), u.getMobileNumber(), u.getAddress(), u.getStatus().name());
    }
}
