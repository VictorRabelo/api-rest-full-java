package com.devrbl.cleanauth.application.dto;

import java.time.Instant;

public record AuthResponseDTO(String accessToken,
                              String refreshToken,
                              String tokenType,
                              Instant expiresAt) {
    public AuthResponseDTO(String accessToken, String refreshToken, Instant expiresAt) {
        this(accessToken, refreshToken, "Bearer", expiresAt);
    }
}
