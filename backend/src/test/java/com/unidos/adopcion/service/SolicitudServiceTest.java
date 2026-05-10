package com.unidos.adopcion.service;

import com.unidos.adopcion.model.*;
import com.unidos.adopcion.model.Mascota.EstadoMascota;
import com.unidos.adopcion.model.SolicitudAdopcion.EstadoSolicitud;
import com.unidos.adopcion.repository.*;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock private SolicitudAdopcionRepository solicitudRepo;
    @Mock private UsuarioRepository usuarioRepo;
    @Mock private MascotaRepository mascotaRepo;

    @InjectMocks
    private SolicitudService solicitudService;

    private Usuario usuario;
    private Mascota mascota;
    private SolicitudAdopcion solicitud;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.cl")
                .password("encoded")
                .build();

        mascota = Mascota.builder()
                .id(1L)
                .nombre("Luna")
                .especie(Mascota.Especie.PERRO)
                .edadMeses(12)
                .genero(Mascota.Genero.HEMBRA)
                .estado(EstadoMascota.DISPONIBLE)
                .build();

        solicitud = SolicitudAdopcion.builder()
                .id(1L)
                .usuario(usuario)
                .mascota(mascota)
                .motivo("Me encanta Luna")
                .estado(EstadoSolicitud.PENDIENTE)
                .build();
    }

    @Test
    void crear_conDatosValidos_debeGuardarSolicitud() {
        when(solicitudRepo.existsByUsuarioIdAndMascotaIdAndEstadoIn(any(), any(), any()))
                .thenReturn(false);
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(mascotaRepo.findById(1L)).thenReturn(Optional.of(mascota));
        when(solicitudRepo.save(any())).thenReturn(solicitud);

        SolicitudAdopcion datos = new SolicitudAdopcion();
        datos.setMotivo("Me encanta Luna");

        SolicitudAdopcion resultado = solicitudService.crear(1L, 1L, datos);

        assertNotNull(resultado);
        assertEquals("Me encanta Luna", resultado.getMotivo());
        verify(solicitudRepo, times(1)).save(any());
    }

    @Test
    void crear_conSolicitudExistente_debeLanzarExcepcion() {
        when(solicitudRepo.existsByUsuarioIdAndMascotaIdAndEstadoIn(any(), any(), any()))
                .thenReturn(true);

        SolicitudAdopcion datos = new SolicitudAdopcion();
        datos.setMotivo("Quiero adoptar");

        assertThrows(IllegalStateException.class,
                () -> solicitudService.crear(1L, 1L, datos));

        verify(solicitudRepo, never()).save(any());
    }

    @Test
    void crear_conMascotaNoDisponible_debeLanzarExcepcion() {
        mascota.setEstado(EstadoMascota.ADOPTADO);

        when(solicitudRepo.existsByUsuarioIdAndMascotaIdAndEstadoIn(any(), any(), any()))
                .thenReturn(false);
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(mascotaRepo.findById(1L)).thenReturn(Optional.of(mascota));

        SolicitudAdopcion datos = new SolicitudAdopcion();
        datos.setMotivo("Quiero adoptar");

        assertThrows(IllegalStateException.class,
                () -> solicitudService.crear(1L, 1L, datos));
    }

    @Test
    void listarPorUsuario_debeRetornarSolicitudesDelUsuario() {
        when(solicitudRepo.findByUsuarioId(1L)).thenReturn(List.of(solicitud));

        List<SolicitudAdopcion> resultado = solicitudService.listarPorUsuario(1L);

        assertEquals(1, resultado.size());
        verify(solicitudRepo, times(1)).findByUsuarioId(1L);
    }

    @Test
    void listarPorEstado_debeRetornarSolicitudesFiltradas() {
        when(solicitudRepo.findByEstado(EstadoSolicitud.PENDIENTE))
                .thenReturn(List.of(solicitud));

        List<SolicitudAdopcion> resultado = solicitudService.listarPorEstado(EstadoSolicitud.PENDIENTE);

        assertEquals(1, resultado.size());
        assertEquals(EstadoSolicitud.PENDIENTE, resultado.get(0).getEstado());
    }

    @Test
    void listarTodas_debeRetornarTodasLasSolicitudes() {
        when(solicitudRepo.findAll()).thenReturn(Arrays.asList(solicitud));

        List<SolicitudAdopcion> resultado = solicitudService.listarTodas();

        assertEquals(1, resultado.size());
        verify(solicitudRepo, times(1)).findAll();
    }

    @Test
    void actualizarEstado_aAprobada_debeActualizarMascotaYSolicitud() {
        when(solicitudRepo.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepo.save(any())).thenReturn(solicitud);
        when(mascotaRepo.save(any())).thenReturn(mascota);

        SolicitudAdopcion resultado = solicitudService.actualizarEstado(1L, EstadoSolicitud.APROBADA);

        assertEquals(EstadoSolicitud.APROBADA, resultado.getEstado());
        assertEquals(EstadoMascota.ADOPTADO, mascota.getEstado());
        verify(mascotaRepo, times(1)).save(mascota);
    }

    @Test
    void actualizarEstado_aRechazada_noDebeActualizarMascota() {
        when(solicitudRepo.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepo.save(any())).thenReturn(solicitud);

        solicitudService.actualizarEstado(1L, EstadoSolicitud.RECHAZADA);

        verify(mascotaRepo, never()).save(any());
    }

    @Test
    void actualizarEstado_conIdInvalido_debeLanzarExcepcion() {
        when(solicitudRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> solicitudService.actualizarEstado(99L, EstadoSolicitud.APROBADA));
    }
}
