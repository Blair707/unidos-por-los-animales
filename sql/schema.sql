-- ============================================================
--  Unidos por los Animales - Esquema de Base de Datos
-- ============================================================

CREATE DATABASE IF NOT EXISTS unidos_animales
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE unidos_animales;

CREATE TABLE roles (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(50)  NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE usuarios (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)    NOT NULL,
    apellido        VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    telefono        VARCHAR(20),
    direccion       VARCHAR(255),
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    fecha_registro  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE usuario_roles (
    usuario_id  BIGINT NOT NULL,
    rol_id      BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES roles (id)
);

CREATE TABLE mascotas (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)    NOT NULL,
    especie         ENUM('PERRO','GATO','OTRO') NOT NULL,
    raza            VARCHAR(100),
    edad_meses      INT             NOT NULL DEFAULT 0,
    genero          ENUM('MACHO','HEMBRA') NOT NULL,
    descripcion     TEXT,
    estado          ENUM('DISPONIBLE','EN_PROCESO','ADOPTADO') NOT NULL DEFAULT 'DISPONIBLE',
    imagen_url      VARCHAR(500),
    fecha_ingreso   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE solicitudes_adopcion (
    id                  BIGINT   NOT NULL AUTO_INCREMENT,
    usuario_id          BIGINT   NOT NULL,
    mascota_id          BIGINT   NOT NULL,
    estado              ENUM('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    motivo              TEXT     NOT NULL,
    tiene_patio         BOOLEAN  NOT NULL DEFAULT FALSE,
    tiene_otras_mascotas BOOLEAN NOT NULL DEFAULT FALSE,
    descripcion_hogar   TEXT,
    fecha_solicitud     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_sol_usuario  FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_sol_mascota  FOREIGN KEY (mascota_id) REFERENCES mascotas (id)
);

CREATE TABLE mensajes (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    remitente_id    BIGINT      NOT NULL,
    destinatario_id BIGINT      NOT NULL,
    solicitud_id    BIGINT,
    asunto          VARCHAR(200),
    contenido       TEXT        NOT NULL,
    leido           BOOLEAN     NOT NULL DEFAULT FALSE,
    fecha_envio     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_msg_remitente    FOREIGN KEY (remitente_id)    REFERENCES usuarios (id),
    CONSTRAINT fk_msg_destinatario FOREIGN KEY (destinatario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_msg_solicitud    FOREIGN KEY (solicitud_id)    REFERENCES solicitudes_adopcion (id)
);


INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_COORDINADOR'), ('ROLE_USUARIO');

-- Contraseña: Admin1234! (BCrypt hash)
INSERT INTO usuarios (nombre, apellido, email, password, activo)
VALUES ('Admin', 'Sistema', 'admin@unidos.cl',
        '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykAGfhaGpgMf4K1ZQ2JRruRUe', TRUE);

INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (1, 1);

INSERT INTO mascotas (nombre, especie, raza, edad_meses, genero, descripcion, estado)
VALUES
    ('Luna',   'PERRO', 'Mestizo',         18,  'HEMBRA', 'Perrita tranquila y cariñosa, buena con niños.',     'DISPONIBLE'),
    ('Max',    'PERRO', 'Labrador',        24,  'MACHO',  'Enérgico y juguetón, necesita espacio.',             'DISPONIBLE'),
    ('Misu',   'GATO',  'Persa',           36,  'HEMBRA', 'Gata independiente, ideal para departamento.',       'DISPONIBLE'),
    ('Simón',  'GATO',  'Mestizo',         12,  'MACHO',  'Gatito joven, muy curioso y sociable.',              'DISPONIBLE'),
    ('Canela', 'PERRO', 'Cocker Spaniel',  48,  'HEMBRA', 'Adulta tranquila, perfecta para personas mayores.', 'DISPONIBLE');
