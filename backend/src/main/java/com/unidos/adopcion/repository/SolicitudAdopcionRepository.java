package com.unidos.adopcion.repository;

import com.unidos.adopcion.model.SolicitudAdopcion;
import com.unidos.adopcion.model.SolicitudAdopcion.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {
    List<SolicitudAdopcion> findByUsuarioId(Long usuarioId);
    List<SolicitudAdopcion> findByEstado(EstadoSolicitud estado);
    boolean existsByUsuarioIdAndMascotaIdAndEstadoIn(
        Long usuarioId, Long mascotaId, List<EstadoSolicitud> estados);
}
