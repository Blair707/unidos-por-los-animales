package com.unidos.adopcion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidos.adopcion.config.JwtAuthFilter;
import com.unidos.adopcion.config.JwtUtils;
import com.unidos.adopcion.model.Mascota;
import com.unidos.adopcion.model.Mascota.EstadoMascota;
import com.unidos.adopcion.model.Mascota.Especie;
import com.unidos.adopcion.service.MascotaService;
import com.unidos.adopcion.service.UsuarioDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MascotaController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class))
class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MascotaService mascotaService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    private Mascota mascota1;
    private Mascota mascota2;

    @BeforeEach
    void setUp() {
        mascota1 = Mascota.builder()
                .id(1L).nombre("Luna").especie(Especie.PERRO)
                .edadMeses(12).genero(Mascota.Genero.HEMBRA)
                .estado(EstadoMascota.DISPONIBLE).build();

        mascota2 = Mascota.builder()
                .id(2L).nombre("Misu").especie(Especie.GATO)
                .edadMeses(24).genero(Mascota.Genero.HEMBRA)
                .estado(EstadoMascota.DISPONIBLE).build();
    }

    @Test
    @WithMockUser
    void listar_sinFiltro_debeRetornarTodasDisponibles() throws Exception {
        when(mascotaService.listarDisponibles()).thenReturn(Arrays.asList(mascota1, mascota2));

        mockMvc.perform(get("/api/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Luna"));
    }

    @Test
    @WithMockUser
    void listar_conFiltroEspecie_debeRetornarFiltrado() throws Exception {
        when(mascotaService.listarPorEspecie(Especie.PERRO)).thenReturn(List.of(mascota1));

        mockMvc.perform(get("/api/mascotas").param("especie", "PERRO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].especie").value("PERRO"));
    }

    @Test
    @WithMockUser
    void detalle_conIdValido_debeRetornarMascota() throws Exception {
        when(mascotaService.buscarPorId(1L)).thenReturn(mascota1);

        mockMvc.perform(get("/api/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Luna"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_conRolAdmin_debeGuardarMascota() throws Exception {
        when(mascotaService.guardar(any())).thenReturn(mascota1);

        mockMvc.perform(post("/api/mascotas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mascota1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Luna"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cambiarEstado_conRolAdmin_debeActualizarEstado() throws Exception {
        mascota1.setEstado(EstadoMascota.ADOPTADO);
        when(mascotaService.actualizarEstado(1L, EstadoMascota.ADOPTADO)).thenReturn(mascota1);

        mockMvc.perform(patch("/api/mascotas/1/estado")
                .with(csrf())
                .param("estado", "ADOPTADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ADOPTADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_conRolAdmin_debeRetornarNoContent() throws Exception {
        doNothing().when(mascotaService).eliminar(1L);

        mockMvc.perform(delete("/api/mascotas/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
