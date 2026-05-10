package com.unidos.adopcion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidos.adopcion.config.JwtAuthFilter;
import com.unidos.adopcion.config.JwtUtils;
import com.unidos.adopcion.model.Usuario;
import com.unidos.adopcion.service.AuthService;
import com.unidos.adopcion.service.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class))
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    @WithMockUser
    void login_conCredencialesValidas_debeRetornarToken() throws Exception {
        when(authService.login(any(), any()))
                .thenReturn(Map.of("token", "jwt-123", "tipo", "Bearer"));

        String body = "{\"email\":\"juan@test.cl\",\"password\":\"Password1!\"}";

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-123"));
    }

    @Test
    @WithMockUser
    void login_conCamposVacios_debeRetornarBadRequest() throws Exception {
        String body = "{\"email\":\"\",\"password\":\"\"}";

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void registro_conDatosValidos_debeRetornarMensaje() throws Exception {
        Usuario usuario = Usuario.builder()
                .nombre("Juan").apellido("Perez")
                .email("juan@test.cl").password("Password1!")
                .build();
        when(authService.registrar(any())).thenReturn(usuario);

        String body = "{\"nombre\":\"Juan\",\"apellido\":\"Perez\"," +
                "\"email\":\"juan@test.cl\",\"password\":\"Password1!\"}";

        mockMvc.perform(post("/api/auth/registro")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @WithMockUser
    void registro_conPasswordCorta_debeRetornarBadRequest() throws Exception {
        String body = "{\"nombre\":\"Juan\",\"apellido\":\"Perez\"," +
                "\"email\":\"juan@test.cl\",\"password\":\"123\"}";

        mockMvc.perform(post("/api/auth/registro")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}
