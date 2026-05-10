package com.unidos.adopcion.controller;

import com.unidos.adopcion.model.Mensaje;
import com.unidos.adopcion.repository.UsuarioRepository;
import com.unidos.adopcion.service.MensajeService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired private MensajeService mensajeService;
    @Autowired private UsuarioRepository usuarioRepo;

    private Long resolverIdUsuario(Authentication auth) {
        return usuarioRepo.findByEmail(auth.getName()).orElseThrow().getId();
    }

    @PostMapping
    public Mensaje enviar(@RequestBody MensajeRequest req, Authentication auth) {
        Long remitenteId = resolverIdUsuario(auth);
        return mensajeService.enviar(remitenteId, req.getDestinatarioId(),
                req.getSolicitudId(), req.getAsunto(), req.getContenido());
    }

    @GetMapping("/entrada")
    public List<Mensaje> entrada(Authentication auth) {
        return mensajeService.bandejaEntrada(resolverIdUsuario(auth));
    }

    @GetMapping("/salida")
    public List<Mensaje> salida(Authentication auth) {
        return mensajeService.bandejaSalida(resolverIdUsuario(auth));
    }

    @GetMapping("/no-leidos")
    public Map<String, Long> noLeidos(Authentication auth) {
        return Map.of("cantidad", mensajeService.contarNoLeidos(resolverIdUsuario(auth)));
    }

    @Data
    static class MensajeRequest {
        private Long destinatarioId;
        private Long solicitudId;
        private String asunto;
        private String contenido;
    }
}
