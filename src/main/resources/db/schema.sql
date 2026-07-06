-- ============================================================
-- SCHEMA SQL V2 - Sistema de Gestión de Requisitos (REM)
-- Base de datos: PostgreSQL 14+
-- Archivo: src/main/resources/db/schema.sql
-- ============================================================
-- Este script crea la estructura completa de la base de datos
-- para el sistema REM v2. Ejecutar en orden secuencial.
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
    ers_markdown TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. Tabla de Stakeholders
-- Partes interesadas vinculadas a los proyectos.
-- Soporta captura estructurada del origen de requisitos.
-- ============================================================
CREATE TABLE stakeholders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    proyecto_id UUID NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    organizacion VARCHAR(100),
    nivel_influencia VARCHAR(20) CHECK (nivel_influencia IN ('BAJO', 'MEDIO', 'ALTO')) DEFAULT 'MEDIO',
    contacto VARCHAR(200),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 4. Tabla de Requisitos
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
    necesidad_cubierta TEXT,
    iteracion_sprint VARCHAR(50),
    criterios_aceptacion TEXT,
    solicitante_id UUID NOT NULL REFERENCES usuarios(id),
    estado VARCHAR(20) CHECK (estado IN ('REGISTRADO', 'EN_ANALISIS', 'VALIDADO', 'APROBADO')) DEFAULT 'REGISTRADO',
    prioridad VARCHAR(20) CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')) DEFAULT 'MEDIA',
    asignado_a_id UUID REFERENCES usuarios(id),
    nivel_ceremonia VARCHAR(10) CHECK (nivel_ceremonia IN ('ALTA', 'BAJA')),
    detalles_caso_uso JSONB,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_requisito_codigo_proyecto UNIQUE (proyecto_id, codigo)
);

-- ============================================================
-- 5. Tabla puente: Requisitos ↔ Stakeholders
-- Mapea qué stakeholder solicitó o está vinculado a un requisito.
-- ============================================================
CREATE TABLE requisitos_stakeholders (
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    stakeholder_id UUID NOT NULL REFERENCES stakeholders(id) ON DELETE CASCADE,
    PRIMARY KEY (requisito_id, stakeholder_id)
);

-- ============================================================
-- 6. Tabla de Historial de Cambios
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
-- 7. Tabla de Dependencias (Trazabilidad entre requisitos)
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
-- 8. Tabla de Comentarios de Revisión (Peer Review)
-- Hilos de discusión vinculados a un requisito.
-- Soporta: Módulo de Validación
-- ============================================================
CREATE TABLE comentarios_review (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 9. Tabla de Checklist de Validación
-- Inspección de calidad antes de aprobar un requisito.
-- Criterios: Atomicidad, Testabilidad, Factibilidad, etc.
-- Soporta: Módulo de Validación
-- ============================================================
CREATE TABLE checklist_validacion (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    evaluador_id UUID NOT NULL REFERENCES usuarios(id),
    es_atomico BOOLEAN DEFAULT FALSE,
    es_testable BOOLEAN DEFAULT FALSE,
    es_factible BOOLEAN DEFAULT FALSE,
    no_ambiguo BOOLEAN DEFAULT FALSE,
    es_completo BOOLEAN DEFAULT FALSE,
    observaciones TEXT,
    fecha_evaluacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 10. Tabla de Solicitudes de Cambio (Change Request)
-- Flujo formal de gestión de cambios (CCB).
-- Soporta: Módulo de Gestión de Cambios
-- ============================================================
CREATE TABLE change_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    solicitante_id UUID NOT NULL REFERENCES usuarios(id),
    justificacion TEXT NOT NULL,
    texto_propuesto TEXT,
    impacto_tecnico TEXT,
    impacto_negocio TEXT,
    riesgos TEXT,
    esfuerzo_estimado VARCHAR(50),
    estado VARCHAR(20) CHECK (estado IN ('PROPUESTO', 'ANALIZADO', 'APROBADO', 'RECHAZADO')) DEFAULT 'PROPUESTO',
    probabilidad_riesgo DECIMAL(5, 2),  -- Calculado por Red Bayesiana (0.00 a 100.00)
    revisado_por_id UUID REFERENCES usuarios(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP
);

-- ============================================================
-- 11. Tabla de Versiones de Requisitos
-- Snapshot inmutable del requisito antes de aplicar un cambio.
-- Soporta: Módulo de Gestión de Cambios (Versionado)
-- ============================================================
CREATE TABLE requisitos_versiones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    version VARCHAR(10) NOT NULL,
    snapshot_json TEXT NOT NULL,
    change_request_id UUID REFERENCES change_requests(id),
    creado_por_id UUID NOT NULL REFERENCES usuarios(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_requisito_version UNIQUE (requisito_id, version)
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
CREATE INDEX idx_stakeholders_proyecto ON stakeholders(proyecto_id);
CREATE INDEX idx_comentarios_requisito ON comentarios_review(requisito_id);
CREATE INDEX idx_comentarios_usuario ON comentarios_review(usuario_id);
CREATE INDEX idx_checklist_requisito ON checklist_validacion(requisito_id);
CREATE INDEX idx_change_requests_requisito ON change_requests(requisito_id);
CREATE INDEX idx_change_requests_estado ON change_requests(estado);
CREATE INDEX idx_versiones_requisito ON requisitos_versiones(requisito_id);

-- ============================================================
-- 12. Tabla de Criterios de Aceptacion (1:N con requisitos)
-- Cada requisito puede tener multiples criterios individuales.
-- ============================================================
CREATE TABLE criterios_aceptacion (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    descripcion TEXT NOT NULL
);

CREATE INDEX idx_criterios_requisito ON criterios_aceptacion(requisito_id);

-- ============================================================
-- 13. Tablas del Asistente de Validacion (Validation Wizard)
-- Agrupa multiples requisitos para revision asincrona.
-- Estados: PENDING, IN_PROGRESS, PAUSED, COMPLETED
-- ============================================================
CREATE TABLE validation_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    proyecto_id UUID NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    estado VARCHAR(20) CHECK (estado IN ('PENDING', 'IN_PROGRESS', 'PAUSED', 'COMPLETED')) DEFAULT 'PENDING',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE validation_session_reviewers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sesion_id UUID NOT NULL REFERENCES validation_sessions(id) ON DELETE CASCADE,
    correo VARCHAR(100) NOT NULL
);

CREATE TABLE validation_session_reqs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sesion_id UUID NOT NULL REFERENCES validation_sessions(id) ON DELETE CASCADE,
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    estado_validacion VARCHAR(20) CHECK (estado_validacion IN ('PENDING', 'APPROVED', 'REJECTED')) DEFAULT 'PENDING',
    observaciones TEXT,
    CONSTRAINT uk_val_sesion_req UNIQUE (sesion_id, requisito_id)
);

CREATE INDEX idx_val_session_proyecto ON validation_sessions(proyecto_id);
CREATE INDEX idx_val_session_req_sesion ON validation_session_reqs(sesion_id);

-- ============================================================
-- 14. Tablas Simuladas de Impacto
-- ============================================================
CREATE TABLE casos_prueba (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    codigo VARCHAR(20) NOT NULL,
    titulo VARCHAR(150) NOT NULL
);

CREATE TABLE objetivos_negocio (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    codigo VARCHAR(20) NOT NULL,
    descripcion TEXT NOT NULL
);
