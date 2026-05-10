package com.unidos.adopcion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mascotas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    private String raza;

    @Column(name = "edad_meses", nullable = false)
    private int edadMeses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMascota estado = EstadoMascota.DISPONIBLE;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private LocalDateTime fechaIngreso = LocalDateTime.now();

    public enum Especie  { PERRO, GATO, OTRO }
    public enum Genero   { MACHO, HEMBRA }
    public enum EstadoMascota { DISPONIBLE, EN_PROCESO, ADOPTADO }
}
