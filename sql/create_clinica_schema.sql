-- Script de creación de esquema para la aplicación "Clinica"
-- Fecha: 2026-01-27
-- Descripción: Crea la base de datos `clinica` y las tablas necesarias
--              (pacientes, tratamientos, internamientos, altas).
-- Recomendación: Ejecútalo con un usuario que tenga privilegios para crear bases y tablas.

-- 1) Crear base de datos
CREATE DATABASE IF NOT EXISTS `clinica`
  DEFAULT CHARACTER SET = utf8mb4
  DEFAULT COLLATE = utf8mb4_unicode_ci;

USE `clinica`;

-- 2) Tabla `pacientes`
-- cod_paciente: se gestiona desde la aplicación (no es AUTOINCREMENT por compatibilidad con los códigos actuales)
CREATE TABLE IF NOT EXISTS `pacientes` (
  `cod_paciente` INT NOT NULL,
  `nombres` VARCHAR(150) NOT NULL,
  `apellidos` VARCHAR(150) NOT NULL,
  `dni` VARCHAR(20) NOT NULL,
  `edad` INT DEFAULT NULL,
  `celular` INT DEFAULT NULL,
  `estado` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`cod_paciente`),
  UNIQUE KEY `uk_pacientes_dni` (`dni`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3) Tabla `tratamientos`
CREATE TABLE IF NOT EXISTS `tratamientos` (
  `cod_tratamiento` INT NOT NULL,
  `nombre_tratamiento` VARCHAR(200) NOT NULL,
  `duracion_dias` INT DEFAULT NULL,
  `sesiones` INT DEFAULT NULL,
  `costo` DECIMAL(10,2) DEFAULT 0.00,
  PRIMARY KEY (`cod_tratamiento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4) Tabla `internamientos`
-- fecha: stored as DATE, hora as TIME for correct type handling. Cuando migres desde .txt convertir la fecha desde dd/MM/yyyy.
CREATE TABLE IF NOT EXISTS `internamientos` (
  `num_internamiento` INT NOT NULL,
  `cod_paciente` INT NOT NULL,
  `cod_tratamiento` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  PRIMARY KEY (`num_internamiento`),
  KEY `idx_internamientos_paciente` (`cod_paciente`),
  KEY `idx_internamientos_tratamiento` (`cod_tratamiento`),
  CONSTRAINT `fk_internamientos_paciente` FOREIGN KEY (`cod_paciente`) REFERENCES `pacientes` (`cod_paciente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_internamientos_tratamiento` FOREIGN KEY (`cod_tratamiento`) REFERENCES `tratamientos` (`cod_tratamiento`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5) Tabla `altas`
CREATE TABLE IF NOT EXISTS `altas` (
  `num_alta` INT NOT NULL,
  `num_internamiento` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  PRIMARY KEY (`num_alta`),
  KEY `idx_altas_internamiento` (`num_internamiento`),
  CONSTRAINT `fk_altas_internamiento` FOREIGN KEY (`num_internamiento`) REFERENCES `internamientos` (`num_internamiento`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6) Opcional: usuario de base de datos (comenta/ajusta según tus políticas de seguridad)
-- CREATE USER 'clinica_user'@'localhost' IDENTIFIED BY 'CambioMiPasswordSeguro';
-- GRANT ALL PRIVILEGES ON `clinica`.* TO 'clinica_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 7) Indices y verificaciones adicionales
-- Asegurar búsqueda rápida por DNI
CREATE INDEX IF NOT EXISTS `idx_pacientes_dni` ON `pacientes` (`dni`);

-- 8) Ejemplos de inserción (opcional):
-- INSERT INTO pacientes (cod_paciente, nombres, apellidos, dni, edad, celular, estado) VALUES (202010001, 'JUAN', 'PEREZ', '12345678', 30, 987654321, 0);

-- 9) Migración desde archivos .txt (guía rápida)
-- Si tienes los archivos planos en el servidor MySQL y el servidor permite LOCAL INFILE, puedes usar:
-- (Asegúrate de que las fechas en los .txt estén en formato yyyy-mm-dd o convierte con una herramienta antes.)
-- Example para pacientes.txt (campos separados por ';' en orden cod;nombres;apellidos;dni;edad;celular;estado):
-- LOAD DATA LOCAL INFILE 'C:/ruta/a/pacientes.txt' INTO TABLE pacientes
-- FIELDS TERMINATED BY ';' LINES TERMINATED BY '\n' (cod_paciente, nombres, apellidos, dni, edad, celular, estado);

-- Si tus fechas están en formato dd/MM/yyyy (como en los .txt actuales), conviene escribir un pequeño script que lea los .txt y genere INSERTs
-- transformando la fecha a yyyy-mm-dd para las tablas `internamientos` y `altas`. Ejemplo de conversión en SQL (usando STR_TO_DATE):
-- LOAD DATA LOCAL INFILE 'C:/ruta/a/internamientos.txt' INTO TABLE internamientos_tmp
-- FIELDS TERMINATED BY ';' LINES TERMINATED BY '\n' (num_internamiento, cod_paciente, cod_tratamiento, @fecha_str, @hora_str)
-- SET fecha = STR_TO_DATE(@fecha_str, '%d/%m/%Y'), hora = STR_TO_DATE(@hora_str, '%H:%i:%s');

-- 10) Comprobación rápida tras la importación
-- SELECT COUNT(*) FROM pacientes;
-- SELECT COUNT(*) FROM tratamientos;
-- SELECT COUNT(*) FROM internamientos;
-- SELECT COUNT(*) FROM altas;

-- Fin del script
