package com.devrbl.cleanauth.presentation;

import com.devrbl.cleanauth.application.dto.AuthRequestDTO;
import com.devrbl.cleanauth.application.dto.AuthResponseDTO;
import com.devrbl.cleanauth.application.service.AuthService;
import com.devrbl.cleanauth.presentation.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @Test
    void loginReturnsTokens() throws Exception {
        when(authService.login(Mockito.any(AuthRequestDTO.class)))
                .thenReturn(new AuthResponseDTO("a", "r", Instant.now().plusSeconds(900)));
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"e@e.com\",\"password\":\"p\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("a"));
    }
}
