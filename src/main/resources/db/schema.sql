-- ============================================================
-- SCHEMA SQL - Sistema de Gestión de Requisitos (REM)
-- Base de datos: PostgreSQL 14+
-- Archivo: src/main/resources/db/schema.sql
-- ============================================================
-- Este script crea la estructura completa de la base de datos
-- para el sistema REM. Ejecutar en orden secuencial.
-- ============================================================

-- Extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- 1. Tabla de Usuarios
-- Almacena los usuarios del sistema con sus roles asignados.
-- Roles válidos: ANALISTA, JEFE_PROYECTO, TECNICO
-- Soporta: RFB-001 a RFB-005 (Autenticación y Autorización)
-- ============================================================
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    rol VARCHAR(20) CHECK (rol IN ('ANALISTA', 'JEFE_PROYECTO', 'TECNICO')) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_conexion TIMESTAMP
);

-- ============================================================
-- 2. Tabla de Proyectos
-- Contenedor principal para la gestión de requisitos.
-- Cada proyecto tiene un jefe de proyecto y un analista.
-- Estados válidos: ACTIVO, PAUSADO, CERRADO
-- Soporta: RFB-006 a RFB-012 (Gestión de Proyectos)
-- ============================================================
CREATE TABLE proyectos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    jefe_proyecto_id UUID NOT NULL REFERENCES usuarios(id),
    analista_id UUID NOT NULL REFERENCES usuarios(id),
    estado VARCHAR(20) CHECK (estado IN ('ACTIVO', 'PAUSADO', 'CERRADO')) DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. Tabla de Requisitos
-- Registro parametrizado de requisitos funcionales (RF)
-- y no funcionales (RNF) asociados a un proyecto.
-- El código es único dentro de cada proyecto (RFB-019).
-- Soporta: RFB-013 a RFB-021 (Gestión de Requisitos)
-- ============================================================
CREATE TABLE requisitos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    proyecto_id UUID NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    codigo VARCHAR(20) NOT NULL,
    tipo VARCHAR(5) CHECK (tipo IN ('RF', 'RNF')) NOT NULL,
    descripcion TEXT NOT NULL,
    solicitante_id UUID NOT NULL REFERENCES usuarios(id),
    estado VARCHAR(20) CHECK (estado IN ('REGISTRADO', 'EN_ANALISIS', 'VALIDADO', 'APROBADO')) DEFAULT 'REGISTRADO',
    prioridad VARCHAR(20) CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')) DEFAULT 'MEDIA',
    costo_estimado DECIMAL(10, 2),
    asignado_a_id UUID REFERENCES usuarios(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_requisito_codigo_proyecto UNIQUE (proyecto_id, codigo)
);

-- ============================================================
-- 4. Tabla de Historial de Cambios
-- Registra de forma inmutable cada modificación realizada
-- sobre un requisito (insert-only, RNFB-029).
-- Soporta: RFB-027 a RFB-031 (Gestión de Cambios)
-- ============================================================
CREATE TABLE requisitos_historial (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    tipo_cambio VARCHAR(10) CHECK (tipo_cambio IN ('INSERT', 'UPDATE', 'DELETE')) NOT NULL,
    campo_modificado VARCHAR(50),
    valor_anterior TEXT,
    valor_nuevo TEXT,
    descripcion_cambio TEXT,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 5. Tabla de Dependencias (Trazabilidad entre requisitos)
-- Permite establecer relaciones de precedencia entre
-- requisitos. Soporta: RF-002 (Precedencia) y RF-003
-- (Trazabilidad). Clave primaria compuesta evita duplicados.
-- ============================================================
CREATE TABLE requisitos_dependencias (
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    requisito_precedente_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    PRIMARY KEY (requisito_id, requisito_precedente_id)
);

-- ============================================================
-- Índices para optimización de consultas frecuentes (RNFB-005)
-- ============================================================
CREATE INDEX idx_requisitos_proyecto ON requisitos(proyecto_id);
CREATE INDEX idx_requisitos_estado ON requisitos(estado);
CREATE INDEX idx_requisitos_tipo ON requisitos(tipo);
CREATE INDEX idx_requisitos_prioridad ON requisitos(prioridad);
CREATE INDEX idx_requisitos_asignado ON requisitos(asignado_a_id);
CREATE INDEX idx_historial_requisito ON requisitos_historial(requisito_id);
CREATE INDEX idx_historial_fecha ON requisitos_historial(fecha_cambio);
CREATE INDEX idx_historial_usuario ON requisitos_historial(usuario_id);
CREATE INDEX idx_proyectos_estado ON proyectos(estado);
CREATE INDEX idx_proyectos_jefe ON proyectos(jefe_proyecto_id);
CREATE INDEX idx_proyectos_analista ON proyectos(analista_id);
