package com.unidos.adopcion.service;

import com.unidos.adopcion.config.JwtUtils;
import com.unidos.adopcion.model.Rol;
import com.unidos.adopcion.model.Usuario;
import com.unidos.adopcion.repository.RolRepository;
import com.unidos.adopcion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private RolRepository rolRepo;
    @Autowired private PasswordEncoder encoder;

    public Map<String, String> login(String email, String password) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        String token = jwtUtils.generarToken(auth);
        return Map.of("token", token, "tipo", "Bearer");
    }

    public Usuario registrar(Usuario datos) {
        if (usuarioRepo.existsByEmail(datos.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        datos.setPassword(encoder.encode(datos.getPassword()));
        Rol rolUsuario = rolRepo.findByNombre("ROLE_USUARIO")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        datos.setRoles(Set.of(rolUsuario));
        return usuarioRepo.save(datos);
    }
}
