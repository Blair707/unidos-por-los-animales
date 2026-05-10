package com.unidos.adopcion.service;

import com.unidos.adopcion.model.*;
import com.unidos.adopcion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeService {

    @Autowired private MensajeRepository mensajeRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private SolicitudAdopcionRepository solicitudRepo;

    public Mensaje enviar(Long remitenteId, Long destinatarioId,
                          Long solicitudId, String asunto, String contenido) {
        Usuario remitente    = usuarioRepo.findById(remitenteId)
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        Usuario destinatario = usuarioRepo.findById(destinatarioId)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        Mensaje msg = Mensaje.builder()
                .remitente(remitente)
                .destinatario(destinatario)
                .asunto(asunto)
                .contenido(contenido)
                .build();

        if (solicitudId != null) {
            solicitudRepo.findById(solicitudId).ifPresent(msg::setSolicitud);
        }
        return mensajeRepo.save(msg);
    }

    public List<Mensaje> bandejaSalida(Long usuarioId) {
        return mensajeRepo.findByRemitenteIdOrderByFechaEnvioDesc(usuarioId);
    }

    public List<Mensaje> bandejaEntrada(Long usuarioId) {
        List<Mensaje> mensajes = mensajeRepo.findByDestinatarioIdOrderByFechaEnvioDesc(usuarioId);
        mensajes.forEach(m -> { m.setLeido(true); mensajeRepo.save(m); });
        return mensajes;
    }

    public long contarNoLeidos(Long usuarioId) {
        return mensajeRepo.countByDestinatarioIdAndLeidoFalse(usuarioId);
    }
}
