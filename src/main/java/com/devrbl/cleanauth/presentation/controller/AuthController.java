package com.devrbl.cleanauth.presentation.controller;

import com.devrbl.cleanauth.application.dto.*;
import com.devrbl.cleanauth.application.service.AuthService;
import com.devrbl.cleanauth.shared.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshHeader
    ) {
        String access = extractBearer(authorization).orElseThrow(() -> new UnauthorizedException("Missing or invalid Authorization header"));
        String refresh = refreshHeader != null ? refreshHeader : request.getHeader("X-Refresh-Token");
        authService.logout(access, refresh);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshHeader,
            @RequestBody(required = false) RefreshTokenRequest body
    ) {
        String refreshToken = refreshHeader != null ? refreshHeader :
                (body != null ? body.refreshToken() : null);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof UserDetails ud) {
            username = ud.getUsername();
        } else if (principal instanceof String s && !"anonymousUser".equalsIgnoreCase(s)) {
            username = s;
        } else {
            throw new UnauthorizedException("Unauthorized");
        }

        return ResponseEntity.ok(authService.me(username));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    private Optional<String> extractBearer(String authorization) {
        if (authorization == null) return Optional.empty();
        String value = authorization.trim();
        if (value.regionMatches(true, 0, "Bearer ", 0, 7) && value.length() > 7) {
            return Optional.of(value.substring(7).trim());
        }
        return Optional.empty();
    }

    public record RefreshTokenRequest(String refreshToken) {}
}
