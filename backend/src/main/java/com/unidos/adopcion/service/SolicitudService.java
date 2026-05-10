package com.unidos.adopcion.service;

import com.unidos.adopcion.model.*;
import com.unidos.adopcion.model.SolicitudAdopcion.EstadoSolicitud;
import com.unidos.adopcion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitudService {

    @Autowired private SolicitudAdopcionRepository solicitudRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private MascotaRepository mascotaRepo;

    @Transactional
    public SolicitudAdopcion crear(Long usuarioId, Long mascotaId, SolicitudAdopcion datos) {
        boolean yaExiste = solicitudRepo.existsByUsuarioIdAndMascotaIdAndEstadoIn(
                usuarioId, mascotaId,
                List.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.EN_REVISION));
        if (yaExiste) {
            throw new IllegalStateException("Ya tienes una solicitud activa para esta mascota.");
        }

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Mascota mascota = mascotaRepo.findById(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (mascota.getEstado() != Mascota.EstadoMascota.DISPONIBLE) {
            throw new IllegalStateException("La mascota no está disponible para adopción.");
        }

        datos.setUsuario(usuario);
        datos.setMascota(mascota);
        datos.setEstado(EstadoSolicitud.PENDIENTE);
        return solicitudRepo.save(datos);
    }

    public List<SolicitudAdopcion> listarPorUsuario(Long usuarioId) {
        return solicitudRepo.findByUsuarioId(usuarioId);
    }

    public List<SolicitudAdopcion> listarPorEstado(EstadoSolicitud estado) {
        return solicitudRepo.findByEstado(estado);
    }

    public List<SolicitudAdopcion> listarTodas() {
        return solicitudRepo.findAll();
    }

    @Transactional
    public SolicitudAdopcion actualizarEstado(Long id, EstadoSolicitud estado) {
        SolicitudAdopcion sol = solicitudRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        sol.setEstado(estado);

        if (estado == EstadoSolicitud.APROBADA) {
            sol.getMascota().setEstado(Mascota.EstadoMascota.ADOPTADO);
            mascotaRepo.save(sol.getMascota());
        }
        return solicitudRepo.save(sol);
    }
}
