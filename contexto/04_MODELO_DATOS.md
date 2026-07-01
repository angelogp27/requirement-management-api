# Modelo de Datos V2 - Sistema de Gestión de Requisitos (REM)

Este documento describe la estructura de la base de datos del sistema REM v2, incluyendo la gestión de proyectos, requisitos, stakeholders, validación, gestión de cambios y trazabilidad.

## 1. Diagrama de Entidad-Relación (Mermaid)

```mermaid
erDiagram
    USUARIOS ||--o{ PROYECTOS : "gestiona (Jefe/Analista)"
    USUARIOS ||--o{ REQUISITOS : "solicita/asigna"
    USUARIOS ||--o{ REQUISITOS_HISTORIAL : "realiza_cambio"
    USUARIOS ||--o{ COMENTARIOS_REVIEW : "escribe"
    USUARIOS ||--o{ CHECKLIST_VALIDACION : "evalua"
    USUARIOS ||--o{ CHANGE_REQUESTS : "solicita/revisa"
    USUARIOS ||--o{ REQUISITOS_VERSIONES : "crea"
    PROYECTOS ||--o{ REQUISITOS : "contiene"
    PROYECTOS ||--o{ STAKEHOLDERS : "tiene"
    REQUISITOS ||--o{ REQUISITOS_HISTORIAL : "tiene"
    REQUISITOS }|--o{ REQUISITOS_DEPENDENCIAS : "depende_de"
    REQUISITOS }|--o{ REQUISITOS_STAKEHOLDERS : "vinculado_a"
    STAKEHOLDERS }|--o{ REQUISITOS_STAKEHOLDERS : "vinculado_a"
    REQUISITOS ||--o{ COMENTARIOS_REVIEW : "tiene"
    REQUISITOS ||--o{ CHECKLIST_VALIDACION : "tiene"
    REQUISITOS ||--o{ CHANGE_REQUESTS : "tiene"
    REQUISITOS ||--o{ REQUISITOS_VERSIONES : "tiene"
    CHANGE_REQUESTS ||--o{ REQUISITOS_VERSIONES : "genera"

    USUARIOS {
        uuid id PK
        string username
        string password
        string email
        string rol "ANALISTA, JEFE_PROYECTO, TECNICO"
        boolean activo
    }

    PROYECTOS {
        uuid id PK
        string codigo UK
        string nombre
        string descripcion
        uuid jefe_proyecto_id FK
        uuid analista_id FK
        string estado "ACTIVO, PAUSADO, CERRADO"
        text ers_markdown "Documento ERS editable"
        timestamp fecha_creacion
    }

    STAKEHOLDERS {
        uuid id PK
        uuid proyecto_id FK
        string nombre
        string rol
        string organizacion
        string nivel_influencia "BAJO, MEDIO, ALTO"
        string contacto
    }

    REQUISITOS {
        uuid id PK
        uuid proyecto_id FK
        string codigo UK
        string tipo "RF, RNF"
        string descripcion
        text necesidad_cubierta "Necesidad de negocio"
        string iteracion_sprint "Sprint asignado"
        text criterios_aceptacion "Criterios Given-When-Then"
        uuid solicitante_id FK
        string estado "REGISTRADO, EN_ANALISIS, VALIDADO, APROBADO"
        string prioridad "BAJA, MEDIA, ALTA, CRITICA"
        decimal costo_estimado
        uuid asignado_a_id FK
    }

    REQUISITOS_STAKEHOLDERS {
        uuid requisito_id FK
        uuid stakeholder_id FK
    }

    REQUISITOS_HISTORIAL {
        uuid id PK
        uuid requisito_id FK
        uuid usuario_id FK
        string tipo_cambio
        string campo_modificado
        text valor_anterior
        text valor_nuevo
    }

    REQUISITOS_DEPENDENCIAS {
        uuid requisito_id FK
        uuid requisito_precedente_id FK
    }

    COMENTARIOS_REVIEW {
        uuid id PK
        uuid requisito_id FK
        uuid usuario_id FK
        text contenido
        timestamp fecha_creacion
    }

    CHECKLIST_VALIDACION {
        uuid id PK
        uuid requisito_id FK
        uuid evaluador_id FK
        boolean es_atomico
        boolean es_testable
        boolean es_factible
        boolean no_ambiguo
        boolean es_completo
        text observaciones
    }

    CHANGE_REQUESTS {
        uuid id PK
        uuid requisito_id FK
        uuid solicitante_id FK
        text justificacion
        text impacto_tecnico
        text impacto_negocio
        text riesgos
        string esfuerzo_estimado
        string estado "PROPUESTO, ANALIZADO, APROBADO, RECHAZADO"
        uuid revisado_por_id FK
    }

    REQUISITOS_VERSIONES {
        uuid id PK
        uuid requisito_id FK
        string version UK
        text snapshot_json
        uuid change_request_id FK
        uuid creado_por_id FK
    }
```

## 2. Tablas (11 en total)

| # | Tabla | Módulo | Descripción |
|---|-------|--------|-------------|
| 1 | `usuarios` | Auth | Usuarios con roles (ANALISTA, JEFE_PROYECTO, TECNICO) |
| 2 | `proyectos` | Core | Proyectos con ERS Markdown integrado |
| 3 | `stakeholders` | **Captura** | Partes interesadas del proyecto |
| 4 | `requisitos` | **Captura** | Requisitos RF/RNF con campos ERS enriquecidos |
| 5 | `requisitos_stakeholders` | **Captura** | Relación N:M Requisitos ↔ Stakeholders |
| 6 | `requisitos_dependencias` | **Especificación** | Trazabilidad entre requisitos |
| 7 | `requisitos_historial` | **Gestión de Cambios** | Auditoría inmutable de cambios |
| 8 | `comentarios_review` | **Validación** | Hilos de discusión (Peer Review) |
| 9 | `checklist_validacion` | **Validación** | Checklist de calidad (5 criterios) |
| 10 | `change_requests` | **Gestión de Cambios** | Solicitudes formales de cambio |
| 11 | `requisitos_versiones` | **Gestión de Cambios** | Snapshots inmutables de versiones |

## 3. Archivos SQL

- **Schema completo:** `src/main/resources/db/schema.sql`
- **Migración incremental:** `src/main/resources/db/migration_v2.sql` (para ejecutar sobre BD existente)

## 4. Evaluación del Modelo V2

| Aspecto | Estado | Observación |
|---|---|---|
| Captura de Stakeholders | ✅ Nuevo | Tabla `stakeholders` + puente `requisitos_stakeholders` |
| Campos ERS enriquecidos | ✅ Nuevo | `necesidad_cubierta`, `iteracion_sprint`, `criterios_aceptacion` |
| Documento ERS Markdown | ✅ Nuevo | Campo `ers_markdown` en `proyectos` |
| Peer Review | ✅ Nuevo | Tabla `comentarios_review` |
| Checklist Validación | ✅ Nuevo | Tabla `checklist_validacion` con 5 criterios booleanos |
| Change Requests (CCB) | ✅ Nuevo | Tabla `change_requests` con análisis de impacto |
| Versionado Explícito | ✅ Nuevo | Tabla `requisitos_versiones` con snapshots JSON |
| Trazabilidad | ✅ Existente | Tabla `requisitos_dependencias` |
| Auditoría | ✅ Existente | Tabla `requisitos_historial` |

> **Resultado:** El modelo V2 cubre completamente los 4 módulos del ciclo de vida de requisitos: Captura, Especificación, Validación y Gestión de Cambios.
