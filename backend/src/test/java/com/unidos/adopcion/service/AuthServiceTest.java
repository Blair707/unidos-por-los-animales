package com.unidos.adopcion.service;

import com.unidos.adopcion.model.Rol;
import com.unidos.adopcion.model.Usuario;
import com.unidos.adopcion.repository.RolRepository;
import com.unidos.adopcion.repository.UsuarioRepository;
import com.unidos.adopcion.config.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private UsuarioRepository usuarioRepo;
    @Mock private RolRepository rolRepo;
    @Mock private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Rol rolUsuario;

    @BeforeEach
    void setUp() {
        rolUsuario = new Rol(1L, "ROLE_USUARIO");

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.cl")
                .password("Password1234!")
                .build();
    }

    @Test
    void login_conCredencialesValidas_debeRetornarToken() {
        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtUtils.generarToken(auth)).thenReturn("jwt-token-123");

        Map<String, String> resultado = authService.login("juan@test.cl", "Password1234!");

        assertNotNull(resultado);
        assertEquals("jwt-token-123", resultado.get("token"));
        assertEquals("Bearer", resultado.get("tipo"));
    }

    @Test
    void registrar_conEmailNuevo_debeGuardarUsuario() {
        when(usuarioRepo.existsByEmail("juan@test.cl")).thenReturn(false);
        when(rolRepo.findByNombre("ROLE_USUARIO")).thenReturn(Optional.of(rolUsuario));
        when(encoder.encode(any())).thenReturn("encoded_password");
        when(usuarioRepo.save(any())).thenReturn(usuario);

        Usuario resultado = authService.registrar(usuario);

        assertNotNull(resultado);
        verify(usuarioRepo, times(1)).save(any());
        verify(encoder, times(1)).encode(any());
    }

    @Test
    void registrar_conEmailExistente_debeLanzarExcepcion() {
        when(usuarioRepo.existsByEmail("juan@test.cl")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.registrar(usuario));

        verify(usuarioRepo, never()).save(any());
    }

    @Test
    void registrar_sinRolDisponible_debeLanzarExcepcion() {
        when(usuarioRepo.existsByEmail("juan@test.cl")).thenReturn(false);
        when(encoder.encode(any())).thenReturn("encoded_password");
        when(rolRepo.findByNombre("ROLE_USUARIO")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.registrar(usuario));
    }
}
