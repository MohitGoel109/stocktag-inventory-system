package com.inventory.api.service;

import com.inventory.api.dto.LoginRequest;
import com.inventory.api.dto.LoginResponse;
import com.inventory.api.entity.AppUser;
import com.inventory.api.repository.AppUserRepository;
import com.inventory.api.security.JwtService;
import com.inventory.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;

    public LoginResponse login(LoginRequest request) {
        // AuthenticationManager handles credential checking (BCrypt comparison)
        // and throws BadCredentialsException / DisabledException as appropriate.
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        AppUser user = principal.getUser();

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole().name(), user.getId());
    }
}
