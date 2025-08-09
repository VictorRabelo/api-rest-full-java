package com.devrbl.cleanauth.application.service.impl;

import com.devrbl.cleanauth.application.dto.*;
import com.devrbl.cleanauth.application.service.AuthService;
import com.devrbl.cleanauth.application.service.TokenBlacklistService;
import com.devrbl.cleanauth.domain.entity.RefreshToken;
import com.devrbl.cleanauth.domain.entity.User;
import com.devrbl.cleanauth.domain.repository.RefreshTokenRepository;
import com.devrbl.cleanauth.domain.repository.UserRepository;
import com.devrbl.cleanauth.infrastructure.security.JwtService;
import com.victor.cleanauth.shared.exception.NotFoundException;
import com.victor.cleanauth.shared.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO login(AuthRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails principal = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        UUID jti = UUID.randomUUID();
        String access = jwtService.generateAccessToken(user, jti);
        String refresh = jwtService.generateRefreshToken(user, UUID.randomUUID());
        refreshTokenRepository.save(RefreshToken.builder()
                .jti(UUID.randomUUID())
                .userId(user.getId())
                .token(refresh)
                .expiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 7))
                .revoked(false)
                .build());
        log.info("User {} logged in", user.getEmail());
        return new AuthResponseDTO(access, refresh, jwtService.getExpiration(access));
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        String jti = jwtService.parse(accessToken).getId();
        blacklistService.blacklist(jti, jwtService.getExpiration(accessToken));
        refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Override
    @Transactional
    public AuthResponseDTO refresh(String refreshToken) {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }
        User user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);
        UUID jti = UUID.randomUUID();
        String access = jwtService.generateAccessToken(user, jti);
        String newRefresh = jwtService.generateRefreshToken(user, UUID.randomUUID());
        refreshTokenRepository.save(RefreshToken.builder()
                .jti(UUID.randomUUID())
                .userId(user.getId())
                .token(newRefresh)
                .expiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 7))
                .revoked(false)
                .build());
        return new AuthResponseDTO(access, newRefresh, jwtService.getExpiration(access));
    }

    @Override
    public MeResponseDTO me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        return new MeResponseDTO(user.getUuid(), user.getName(), user.getEmail(), roles);
    }
}
