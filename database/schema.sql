-- Radio Jugoseo
-- Base de datos exclusiva del microservicio de programas

CREATE DATABASE IF NOT EXISTS radiojugoseo_programas
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE radiojugoseo_programas;

-- Programas de la radio (parrilla semanal)
CREATE TABLE IF NOT EXISTS programas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    locutor_id BIGINT NULL,            -- id del usuario (microservicio de usuarios)
    dia_semana VARCHAR(15) NOT NULL,   -- MONDAY ... SUNDAY
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Transmisiones simuladas de un programa
CREATE TABLE IF NOT EXISTS transmisiones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    programa_id BIGINT NOT NULL,
    estado VARCHAR(15) NOT NULL,       -- EN_VIVO | FINALIZADA
    fecha_inicio TIMESTAMP(3) NOT NULL,
    fecha_fin TIMESTAMP(3) NULL,
    oyentes_actuales INT NOT NULL DEFAULT 0,
    oyentes_pico INT NOT NULL DEFAULT 0,
    iniciada_por BIGINT NULL,

    CONSTRAINT fk_transmision_programa
        FOREIGN KEY (programa_id)
        REFERENCES programas(id)
);
