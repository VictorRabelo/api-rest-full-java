package com.devrbl.cleanauth.presentation.controller;

import com.devrbl.cleanauth.application.dto.*;
import com.devrbl.cleanauth.application.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> logout(HttpServletRequest request, @RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        String access = header.substring(7);
        String refresh = request.getHeader("X-Refresh-Token");
        authService.logout(access, refresh);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(authService.me(user.getUsername()));
    }
}
