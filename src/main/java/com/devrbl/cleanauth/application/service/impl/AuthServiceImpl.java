package com.devrbl.cleanauth.application.service.impl;

import com.devrbl.cleanauth.application.dto.AuthRequestDTO;
import com.devrbl.cleanauth.application.dto.AuthResponseDTO;
import com.devrbl.cleanauth.application.dto.MeResponseDTO;
import com.devrbl.cleanauth.application.dto.RegisterRequestDTO;
import com.devrbl.cleanauth.application.service.AuthService;
import com.devrbl.cleanauth.application.service.TokenBlacklistService;
import com.devrbl.cleanauth.domain.entity.RefreshToken;
import com.devrbl.cleanauth.domain.entity.Role;
import com.devrbl.cleanauth.domain.entity.User;
import com.devrbl.cleanauth.domain.repository.RefreshTokenRepository;
import com.devrbl.cleanauth.domain.repository.UserRepository;
import com.devrbl.cleanauth.infrastructure.security.JwtService;
import com.devrbl.cleanauth.shared.exception.ConflictException;
import com.devrbl.cleanauth.shared.exception.NotFoundException;
import com.devrbl.cleanauth.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // ----------------- AUTH -----------------

    @Override
    @Transactional
    public AuthResponseDTO login(AuthRequestDTO request) {
        final String email = normalizeEmail(request.email());

        // Pré-cheque de diagnóstico (aparece em dev)
        userRepository.findByEmailIgnoreCase(email).ifPresentOrElse(u -> {
            boolean matches = safeMatches(request.password(), u.getPassword());
            log.info("PRE-AUTH: userFound=true, bcryptMatches={}", matches);
        }, () -> log.info("PRE-AUTH: userFound=false"));

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );

            UserDetails principal = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByEmailIgnoreCase(principal.getUsername())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            return issueTokens(user);
        } catch (BadCredentialsException e) {
            log.warn("AUTH FAIL (bad credentials) for email={}", email);
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new UnauthorizedException("Missing access token");
        }
        // Blacklist do JTI do access token
        String jti = jwtService.parse(accessToken).getId();
        blacklistService.blacklist(jti, jwtService.getExpiration(accessToken));

        // Revoga refresh token se informado
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
                if (!rt.isRevoked()) {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                }
            });
        }
    }

    @Override
    @Transactional
    public AuthResponseDTO refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }

        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        User user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Rotaciona o refresh token
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        return issueTokens(user);
    }

    @Override
    @Transactional(readOnly = true)
    public MeResponseDTO me(String email) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new MeResponseDTO(user.getUuid(), user.getName(), user.getEmail(), roles);
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already registered");
        }

        User user = User.builder()
                .uuid(UUID.randomUUID().toString())
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(Role.ADMIN))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        userRepository.save(user);
        return issueTokens(user);
    }

    // ----------------- HELPERS -----------------

    private AuthResponseDTO issueTokens(User user) {
        UUID jti = UUID.randomUUID();
        String access = jwtService.generateAccessToken(user, jti);
        String refresh = jwtService.generateRefreshToken(user, UUID.randomUUID());

        refreshTokenRepository.save(RefreshToken.builder()
                .jti(UUID.randomUUID())
                .userId(user.getId())
                .token(refresh)
                .expiresAt(Instant.now().plus(REFRESH_TTL))
                .revoked(false)
                .build()
        );

        return new AuthResponseDTO(access, refresh, jwtService.getExpiration(access));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private boolean safeMatches(String raw, String hash) {
        try {
            return passwordEncoder.matches(raw, hash);
        } catch (Exception e) {
            log.debug("BCrypt match failed: {}", e.getMessage());
            return false;
        }
    }
}
