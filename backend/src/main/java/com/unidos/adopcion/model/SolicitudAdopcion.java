package com.unidos.adopcion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_adopcion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "tiene_patio")
    private boolean tienePatio;

    @Column(name = "tiene_otras_mascotas")
    private boolean tieneOtrasMascotas;

    @Column(name = "descripcion_hogar", columnDefinition = "TEXT")
    private String descripcionHogar;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public enum EstadoSolicitud { PENDIENTE, EN_REVISION, APROBADA, RECHAZADA }
}
