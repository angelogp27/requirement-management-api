-- ============================================================
-- MIGRACIÓN V2 - Sistema de Gestión de Requisitos (REM)
-- Ejecutar sobre la BD existente con schema.sql V1 aplicado.
-- ============================================================

-- ============================================================
-- 1. Nuevas columnas en tabla REQUISITOS
-- Campos requeridos por el ERS: necesidad que cubre,
-- iteración/sprint, criterios de aceptación.
-- ============================================================
ALTER TABLE requisitos
    ADD COLUMN necesidad_cubierta TEXT,
    ADD COLUMN iteracion_sprint VARCHAR(50),
    ADD COLUMN criterios_aceptacion TEXT;

-- ============================================================
-- 2. Nueva columna en tabla PROYECTOS
-- Almacena el documento ERS en formato Markdown editable.
-- ============================================================
ALTER TABLE proyectos
    ADD COLUMN ers_markdown TEXT;

-- ============================================================
-- 3. Tabla de Stakeholders
-- Partes interesadas vinculadas a los proyectos.
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

CREATE INDEX idx_stakeholders_proyecto ON stakeholders(proyecto_id);

-- ============================================================
-- 4. Tabla puente: Requisitos ↔ Stakeholders
-- Mapea qué stakeholder solicitó o está vinculado a un requisito.
-- ============================================================
CREATE TABLE requisitos_stakeholders (
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    stakeholder_id UUID NOT NULL REFERENCES stakeholders(id) ON DELETE CASCADE,
    PRIMARY KEY (requisito_id, stakeholder_id)
);

-- ============================================================
-- 5. Tabla de Comentarios de Revisión (Peer Review)
-- Hilos de discusión vinculados a un requisito.
-- ============================================================
CREATE TABLE comentarios_review (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_comentarios_requisito ON comentarios_review(requisito_id);
CREATE INDEX idx_comentarios_usuario ON comentarios_review(usuario_id);

-- ============================================================
-- 6. Tabla de Checklist de Validación
-- Inspección de calidad antes de aprobar un requisito.
-- Criterios: Atomicidad, Testabilidad, Factibilidad, etc.
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

CREATE INDEX idx_checklist_requisito ON checklist_validacion(requisito_id);

-- ============================================================
-- 7. Tabla de Solicitudes de Cambio (Change Request)
-- Flujo formal de gestión de cambios (CCB).
-- ============================================================
CREATE TABLE change_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requisito_id UUID NOT NULL REFERENCES requisitos(id) ON DELETE CASCADE,
    solicitante_id UUID NOT NULL REFERENCES usuarios(id),
    justificacion TEXT NOT NULL,
    impacto_tecnico TEXT,
    impacto_negocio TEXT,
    riesgos TEXT,
    esfuerzo_estimado VARCHAR(50),
    estado VARCHAR(20) CHECK (estado IN ('PROPUESTO', 'ANALIZADO', 'APROBADO', 'RECHAZADO')) DEFAULT 'PROPUESTO',
    revisado_por_id UUID REFERENCES usuarios(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP
);

CREATE INDEX idx_change_requests_requisito ON change_requests(requisito_id);
CREATE INDEX idx_change_requests_estado ON change_requests(estado);

-- ============================================================
-- 8. Tabla de Versiones de Requisitos
-- Snapshot inmutable del requisito antes de aplicar un cambio.
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

CREATE INDEX idx_versiones_requisito ON requisitos_versiones(requisito_id);
