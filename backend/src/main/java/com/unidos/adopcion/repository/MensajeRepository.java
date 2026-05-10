package com.unidos.adopcion.repository;

import com.unidos.adopcion.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByDestinatarioIdOrderByFechaEnvioDesc(Long destinatarioId);
    List<Mensaje> findByRemitenteIdOrderByFechaEnvioDesc(Long remitenteId);
    long countByDestinatarioIdAndLeidoFalse(Long destinatarioId);
}
