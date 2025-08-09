package com.devrbl.cleanauth.application.service;

import com.devrbl.cleanauth.application.dto.*;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO request);
    void logout(String accessToken, String refreshToken);
    AuthResponseDTO refresh(String refreshToken);
    MeResponseDTO me(String email);
}
