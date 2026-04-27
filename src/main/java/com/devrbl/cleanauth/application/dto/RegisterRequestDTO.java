package com.devrbl.cleanauth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 6, max = 72) String password
) {}
