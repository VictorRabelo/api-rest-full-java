package com.devrbl.cleanauth.application.dto;

import java.time.Instant;

public record ClientResponseDTO(
        String uuid,
        String name,
        String email,
        String phone,
        String birthDate,
        Instant createdAt,
        Instant updatedAt
) {}
