package com.unidos.adopcion.controller;

import com.unidos.adopcion.model.Usuario;
import com.unidos.adopcion.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req.getEmail(), req.getPassword()));
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registro(@Valid @RequestBody RegistroRequest req) {
        Usuario u = Usuario.builder()
                .nombre(req.getNombre())
                .apellido(req.getApellido())
                .email(req.getEmail())
                .password(req.getPassword())
                .telefono(req.getTelefono())
                .direccion(req.getDireccion())
                .build();
        authService.registrar(u);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado correctamente."));
    }

    @Data
    static class LoginRequest {
        @NotBlank @Email  private String email;
        @NotBlank         private String password;
    }

    @Data
    static class RegistroRequest {
        @NotBlank private String nombre;
        @NotBlank private String apellido;
        @NotBlank @Email  private String email;
        @NotBlank @Size(min = 8) private String password;
        private String telefono;
        private String direccion;
    }
}
