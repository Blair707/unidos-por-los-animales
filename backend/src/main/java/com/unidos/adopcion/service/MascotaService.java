package com.unidos.adopcion.service;

import com.unidos.adopcion.model.Mascota;
import com.unidos.adopcion.model.Mascota.EstadoMascota;
import com.unidos.adopcion.model.Mascota.Especie;
import com.unidos.adopcion.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepo;

    public List<Mascota> listarDisponibles() {
        return mascotaRepo.findByEstado(EstadoMascota.DISPONIBLE);
    }

    public List<Mascota> listarPorEspecie(Especie especie) {
        return mascotaRepo.findByEspecieAndEstado(especie, EstadoMascota.DISPONIBLE);
    }

    public Mascota buscarPorId(Long id) {
        return mascotaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada: " + id));
    }

    public Mascota guardar(Mascota mascota) {
        return mascotaRepo.save(mascota);
    }

    public Mascota actualizarEstado(Long id, EstadoMascota estado) {
        Mascota m = buscarPorId(id);
        m.setEstado(estado);
        return mascotaRepo.save(m);
    }

    public void eliminar(Long id) {
        mascotaRepo.deleteById(id);
    }

    public List<Mascota> listarTodas() {
        return mascotaRepo.findAll();
    }
}
