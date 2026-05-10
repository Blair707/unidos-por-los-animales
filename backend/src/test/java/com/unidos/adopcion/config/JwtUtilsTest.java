package com.unidos.adopcion.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private static final String SECRET =
            "UnidosPorLosAnimalesSecretKey2024ChileAdopcionSegura";
    private static final int EXPIRATION_MS = 86400000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", EXPIRATION_MS);
    }

    @Test
    void generarToken_debeRetornarTokenNoNulo() {
        UserDetails userDetails = new User("juan@test.cl", "pass", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generarToken(authentication);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void getEmailDesdeToken_debeRetornarEmailCorrecto() {
        UserDetails userDetails = new User("juan@test.cl", "pass", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generarToken(authentication);

        String email = jwtUtils.getEmailDesdeToken(token);

        assertEquals("juan@test.cl", email);
    }

    @Test
    void validarToken_conTokenValido_debeRetornarTrue() {
        UserDetails userDetails = new User("juan@test.cl", "pass", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generarToken(authentication);

        assertTrue(jwtUtils.validarToken(token));
    }

    @Test
    void validarToken_conTokenInvalido_debeRetornarFalse() {
        assertFalse(jwtUtils.validarToken("esto.no.es.un.jwt.valido"));
    }

    @Test
    void validarToken_conTokenVacio_debeRetornarFalse() {
        assertFalse(jwtUtils.validarToken(""));
    }

    @Test
    void validarToken_conTokenManipulado_debeRetornarFalse() {
        UserDetails userDetails = new User("juan@test.cl", "pass", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generarToken(authentication);
        String tokenManipulado = token.substring(0, token.length() - 5) + "XXXXX";

        assertFalse(jwtUtils.validarToken(tokenManipulado));
    }
}
