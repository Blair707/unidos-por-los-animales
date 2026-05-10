package com.unidos.adopcion.repository;

import com.unidos.adopcion.model.Mascota;
import com.unidos.adopcion.model.Mascota.EstadoMascota;
import com.unidos.adopcion.model.Mascota.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByEstado(EstadoMascota estado);
    List<Mascota> findByEspecieAndEstado(Especie especie, EstadoMascota estado);
}
