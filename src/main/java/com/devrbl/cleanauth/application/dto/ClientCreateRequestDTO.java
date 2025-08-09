package com.devrbl.cleanauth.application.dto;

import jakarta.validation.constraints.Email;

public record ClientCreateRequestDTO(
        String name,
        @Email String email,
        String phone,
        String birthDate
) {}
