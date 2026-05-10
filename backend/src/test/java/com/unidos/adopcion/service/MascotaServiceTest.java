package com.unidos.adopcion.service;

import com.unidos.adopcion.model.Mascota;
import com.unidos.adopcion.model.Mascota.EstadoMascota;
import com.unidos.adopcion.model.Mascota.Especie;
import com.unidos.adopcion.repository.MascotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private MascotaService mascotaService;

    private Mascota mascota1;
    private Mascota mascota2;

    @BeforeEach
    void setUp() {
        mascota1 = Mascota.builder()
                .id(1L)
                .nombre("Luna")
                .especie(Especie.PERRO)
                .raza("Mestizo")
                .edadMeses(12)
                .genero(Mascota.Genero.HEMBRA)
                .estado(EstadoMascota.DISPONIBLE)
                .build();

        mascota2 = Mascota.builder()
                .id(2L)
                .nombre("Misu")
                .especie(Especie.GATO)
                .raza("Persa")
                .edadMeses(24)
                .genero(Mascota.Genero.HEMBRA)
                .estado(EstadoMascota.DISPONIBLE)
                .build();
    }

    @Test
    void listarDisponibles_debeRetornarMascotasDisponibles() {
        when(mascotaRepository.findByEstado(EstadoMascota.DISPONIBLE))
                .thenReturn(Arrays.asList(mascota1, mascota2));

        List<Mascota> resultado = mascotaService.listarDisponibles();

        assertEquals(2, resultado.size());
        verify(mascotaRepository, times(1)).findByEstado(EstadoMascota.DISPONIBLE);
    }

    @Test
    void listarPorEspecie_debeRetornarMascotasFiltradas() {
        when(mascotaRepository.findByEspecieAndEstado(Especie.PERRO, EstadoMascota.DISPONIBLE))
                .thenReturn(List.of(mascota1));

        List<Mascota> resultado = mascotaService.listarPorEspecie(Especie.PERRO);

        assertEquals(1, resultado.size());
        assertEquals("Luna", resultado.get(0).getNombre());
        verify(mascotaRepository, times(1))
                .findByEspecieAndEstado(Especie.PERRO, EstadoMascota.DISPONIBLE);
    }

    @Test
    void buscarPorId_conIdValido_debeRetornarMascota() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota1));

        Mascota resultado = mascotaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Luna", resultado.getNombre());
        assertEquals(Especie.PERRO, resultado.getEspecie());
    }

    @Test
    void buscarPorId_conIdInvalido_debeLanzarExcepcion() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mascotaService.buscarPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void guardar_debeRetornarMascotaGuardada() {
        when(mascotaRepository.save(mascota1)).thenReturn(mascota1);

        Mascota resultado = mascotaService.guardar(mascota1);

        assertNotNull(resultado);
        assertEquals("Luna", resultado.getNombre());
        verify(mascotaRepository, times(1)).save(mascota1);
    }

    @Test
    void actualizarEstado_debeActualizarCorrectamente() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota1));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota1);

        Mascota resultado = mascotaService.actualizarEstado(1L, EstadoMascota.ADOPTADO);

        assertEquals(EstadoMascota.ADOPTADO, resultado.getEstado());
        verify(mascotaRepository, times(1)).save(mascota1);
    }

    @Test
    void eliminar_debeInvocarDeleteById() {
        doNothing().when(mascotaRepository).deleteById(1L);

        mascotaService.eliminar(1L);

        verify(mascotaRepository, times(1)).deleteById(1L);
    }

    @Test
    void listarTodas_debeRetornarTodasLasMascotas() {
        when(mascotaRepository.findAll()).thenReturn(Arrays.asList(mascota1, mascota2));

        List<Mascota> resultado = mascotaService.listarTodas();

        assertEquals(2, resultado.size());
        verify(mascotaRepository, times(1)).findAll();
    }
}
