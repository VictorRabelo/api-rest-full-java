package com.devrbl.cleanauth.application.dto;

import jakarta.validation.constraints.Email;

public record ClientUpdateRequestDTO(
        String name,
        @Email String email,
        String phone,
        String birthDate
) {}
