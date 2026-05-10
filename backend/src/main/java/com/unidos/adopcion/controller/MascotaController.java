package com.unidos.adopcion.controller;

import com.unidos.adopcion.model.Mascota;
import com.unidos.adopcion.model.Mascota.Especie;
import com.unidos.adopcion.model.Mascota.EstadoMascota;
import com.unidos.adopcion.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    /** Público: listar mascotas disponibles */
    @GetMapping
    public List<Mascota> listar(@RequestParam(required = false) Especie especie) {
        if (especie != null) return mascotaService.listarPorEspecie(especie);
        return mascotaService.listarDisponibles();
    }

    /** Público: detalle de mascota */
    @GetMapping("/{id}")
    public Mascota detalle(@PathVariable Long id) {
        return mascotaService.buscarPorId(id);
    }

    /** Admin/Coordinador: listar todas */
    @GetMapping("/admin/todas")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    public List<Mascota> todas() {
        return mascotaService.listarTodas();
    }

    /** Admin/Coordinador: crear */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    public Mascota crear(@RequestBody Mascota mascota) {
        return mascotaService.guardar(mascota);
    }

    /** Admin/Coordinador: actualizar */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    public Mascota actualizar(@PathVariable Long id, @RequestBody Mascota datos) {
        datos.setId(id);
        return mascotaService.guardar(datos);
    }

    /** Admin/Coordinador: cambiar estado */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    public Mascota cambiarEstado(@PathVariable Long id, @RequestParam EstadoMascota estado) {
        return mascotaService.actualizarEstado(id, estado);
    }

    /** Admin: eliminar */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
