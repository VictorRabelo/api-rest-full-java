package com.devrbl.cleanauth.application.dto;

import java.util.Set;

public record MeResponseDTO(String uuid,
                            String name,
                            String email,
                            Set<String> roles) {
}
