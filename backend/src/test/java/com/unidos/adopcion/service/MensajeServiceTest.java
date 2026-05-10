package com.unidos.adopcion.service;

import com.unidos.adopcion.model.Mensaje;
import com.unidos.adopcion.model.Usuario;
import com.unidos.adopcion.repository.MensajeRepository;
import com.unidos.adopcion.repository.SolicitudAdopcionRepository;
import com.unidos.adopcion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock private MensajeRepository mensajeRepo;
    @Mock private UsuarioRepository usuarioRepo;
    @Mock private SolicitudAdopcionRepository solicitudRepo;

    @InjectMocks
    private MensajeService mensajeService;

    private Usuario remitente;
    private Usuario destinatario;
    private Mensaje mensaje;

    @BeforeEach
    void setUp() {
        remitente = Usuario.builder()
                .id(1L).nombre("Juan").apellido("Perez")
                .email("juan@test.cl").password("encoded").build();

        destinatario = Usuario.builder()
                .id(2L).nombre("Maria").apellido("Lopez")
                .email("maria@test.cl").password("encoded").build();

        mensaje = Mensaje.builder()
                .id(1L)
                .remitente(remitente)
                .destinatario(destinatario)
                .asunto("Consulta adopcion")
                .contenido("Hola, me interesa adoptar a Luna.")
                .leido(false)
                .build();
    }

    @Test
    void enviar_conDatosValidos_debeGuardarMensaje() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(remitente));
        when(usuarioRepo.findById(2L)).thenReturn(Optional.of(destinatario));
        when(mensajeRepo.save(any())).thenReturn(mensaje);

        Mensaje resultado = mensajeService.enviar(1L, 2L, null,
                "Consulta adopcion", "Hola, me interesa adoptar a Luna.");

        assertNotNull(resultado);
        assertEquals("Consulta adopcion", resultado.getAsunto());
        verify(mensajeRepo, times(1)).save(any());
    }

    @Test
    void enviar_conRemitenteInvalido_debeLanzarExcepcion() {
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> mensajeService.enviar(99L, 2L, null, "Asunto", "Contenido"));
    }

    @Test
    void enviar_conDestinatarioInvalido_debeLanzarExcepcion() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(remitente));
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> mensajeService.enviar(1L, 99L, null, "Asunto", "Contenido"));
    }

    @Test
    void bandejaEntrada_debeRetornarMensajesYMarcarLeidos() {
        when(mensajeRepo.findByDestinatarioIdOrderByFechaEnvioDesc(2L))
                .thenReturn(List.of(mensaje));
        when(mensajeRepo.save(any())).thenReturn(mensaje);

        List<Mensaje> resultado = mensajeService.bandejaEntrada(2L);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isLeido());
        verify(mensajeRepo, times(1)).save(any());
    }

    @Test
    void bandejaSalida_debeRetornarMensajesEnviados() {
        when(mensajeRepo.findByRemitenteIdOrderByFechaEnvioDesc(1L))
                .thenReturn(List.of(mensaje));

        List<Mensaje> resultado = mensajeService.bandejaSalida(1L);

        assertEquals(1, resultado.size());
        assertEquals("Consulta adopcion", resultado.get(0).getAsunto());
    }

    @Test
    void contarNoLeidos_debeRetornarCantidadCorrecta() {
        when(mensajeRepo.countByDestinatarioIdAndLeidoFalse(2L)).thenReturn(3L);

        long cantidad = mensajeService.contarNoLeidos(2L);

        assertEquals(3L, cantidad);
        verify(mensajeRepo, times(1)).countByDestinatarioIdAndLeidoFalse(2L);
    }
}
