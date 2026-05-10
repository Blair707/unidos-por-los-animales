package com.unidos.adopcion.controller;

import com.unidos.adopcion.model.SolicitudAdopcion;
import com.unidos.adopcion.model.SolicitudAdopcion.EstadoSolicitud;
import com.unidos.adopcion.repository.UsuarioRepository;
import com.unidos.adopcion.service.SolicitudService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired private SolicitudService solicitudService;
    @Autowired private UsuarioRepository usuarioRepo;

    /** Usuario: crear solicitud */
    @PostMapping
    public SolicitudAdopcion crear(@RequestBody SolicitudRequest req, Authentication auth) {
        Long usuarioId = usuarioRepo.findByEmail(auth.getName())
                .orElseThrow().getId();

        SolicitudAdopcion datos = new SolicitudAdopcion();
        datos.setMotivo(req.getMotivo());
        datos.setTienePatio(req.isTienePatio());
        datos.setTieneOtrasMascotas(req.isTieneOtrasMascotas());
        datos.setDescripcionHogar(req.getDescripcionHogar());

        return solicitudService.crear(usuarioId, req.getMascotaId(), datos);
    }

    /** Usuario: mis solicitudes */
    @GetMapping("/mis-solicitudes")
    public List<SolicitudAdopcion> misSolicitudes(Authentication auth) {
        Long usuarioId = usuarioRepo.findByEmail(auth.getName())
                .orElseThrow().getId();
        return solicitudService.listarPorUsuario(usuarioId);
    }

    /** Admin/Coordinador: listar todas */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    public List<SolicitudAdopcion> todas(@RequestParam(required = false) EstadoSolicitud estado) {
        if (estado != null) return solicitudService.listarPorEstado(estado);
        return solicitudService.listarTodas();
    }

    /** Admin/Coordinador: cambiar estado */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    public SolicitudAdopcion cambiarEstado(@PathVariable Long id,
                                           @RequestParam EstadoSolicitud estado) {
        return solicitudService.actualizarEstado(id, estado);
    }

    @Data
    static class SolicitudRequest {
        private Long mascotaId;
        private String motivo;
        private boolean tienePatio;
        private boolean tieneOtrasMascotas;
        private String descripcionHogar;
    }
}
