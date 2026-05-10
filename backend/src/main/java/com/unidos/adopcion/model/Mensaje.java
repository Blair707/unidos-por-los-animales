package com.unidos.adopcion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "remitente_id")
    private Usuario remitente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_id")
    private Usuario destinatario;

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private SolicitudAdopcion solicitud;

    @Column(length = 200)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private boolean leido = false;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();
}
