# ÍNDICE Y GUÍA DE NAVEGACIÓN
## Sistema de Gestión de Requisitos - Requirement Management (REM)

---

## 📋 Contenido de la Documentación

Este conjunto de documentos define de manera **clara, precisa y verificable** todos los requisitos para el desarrollo del sistema **Requirement Management (REM)** según el estándar **IEEE 830** y las mejores prácticas de ingeniería de software.

La documentación se divide en **4 archivos Markdown principales**:

---

## 📄 1. ERS - Especificación de Requisitos de Software
**Archivo:** `01_ERS_Requirement_Management.md`

### Contenido
- ✅ Propósito y ámbito del sistema
- ✅ Definiciones, acrónimos y abreviaturas
- ✅ Descripción general del producto
- ✅ Características de usuarios
- ✅ Restricciones y suposiciones
- ✅ Requisitos específicos funcionales
- ✅ Requisitos de rendimiento
- ✅ Restricciones de diseño
- ✅ Atributos de calidad del sistema
- ✅ Matriz de trazabilidad de requisitos
- ✅ Aprobaciones y validaciones

### Para Quién
- **Clientes y Stakeholders:** Entender qué sistema se está construyendo
- **Project Managers:** Seguimiento de requisitos y aprobaciones
- **Analistas de Requisitos:** Base para análisis y validación
- **Todos los Desarrolladores:** Referencia de qué debe hacer el sistema

### Secciones Clave
| Sección | Propósito |
|---|---|
| 1. Introducción | Contexto y propósito del ERS |
| 2. Descripción General | Visión del producto sin detalles técnicos |
| 3. Requisitos Específicos | Funcionalidades y restricciones detalladas |
| 4. Matriz de Trazabilidad | Tracking de requisitos |
| 5. Aprobaciones | Validación del cliente |

---

## 🔙 2. DESARROLLO BACKEND - Spring Boot
**Archivo:** `02_DESARROLLO_Backend_SpringBoot.md`

### Contenido
- ✅ Stack tecnológico recomendado
- ✅ Arquitectura Cliente-Servidor REST
- ✅ Estructura de carpetas de proyecto
- ✅ 40+ Requisitos funcionales del backend
- ✅ Requisitos no funcionales (rendimiento, seguridad, etc.)
- ✅ Especificación de 20+ endpoints RESTful
- ✅ Modelo de datos completo (5 tablas SQL)
- ✅ Configuración de Spring Boot
- ✅ Dependencias Maven (pom.xml)
- ✅ application.properties de configuración
- ✅ Plan de testing y cobertura
- ✅ Matriz de trazabilidad ERS↔Backend
- ✅ Checklist de implementación

### Para Quién
- **Desarrolladores Backend:** Guía técnica de implementación
- **Arquitectos de Software:** Decisiones de diseño justificadas
- **Equipo QA:** Casos de prueba y validación
- **DevOps:** Configuración y deployment

### Tecnologías
| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17+ | Lenguaje principal |
| Spring Boot | 3.1.x | Framework web |
| PostgreSQL | 14+ | Base de datos |
| JWT | 0.11.5 | Autenticación |
| Docx4j | 11.4.11 | Generación de documentos |
| JUnit 5 | Latest | Testing unitario |
| Mockito | Latest | Mocks en tests |

### Endpoints Principales
- `POST /api/auth/login` - Autenticación
- `POST /api/projects` - Crear proyecto
- `GET /api/projects/{id}/requirements` - Listar requisitos
- `PUT /api/projects/{id}/requirements/{reqId}` - Actualizar requisito
- `POST /api/projects/{id}/export/ers` - Exportar documento

---

## 🎨 3. DESARROLLO FRONTEND - Angular
**Archivo:** `03_DESARROLLO_Frontend_Angular.md`

### Contenido
- ✅ Stack tecnológico recomendado
- ✅ Arquitectura SPA (Single Page Application)
- ✅ Estructura de carpetas Angular
- ✅ 50+ Requisitos funcionales del frontend
- ✅ Requisitos no funcionales (UX, accesibilidad, seguridad)
- ✅ Componentes principales especificados
- ✅ Servicios y su implementación
- ✅ Interceptores (autenticación, manejo de errores)
- ✅ Rutas y lazy loading
- ✅ Modelos TypeScript (DTOs)
- ✅ Estilos y paleta de colores
- ✅ Plan de testing (unitario + E2E)
- ✅ Matriz de trazabilidad ERS↔Frontend
- ✅ Checklist de implementación

### Para Quién
- **Desarrolladores Frontend:** Especificación de componentes y servicios
- **UI/UX Designers:** Guías de diseño responsivo
- **Equipo QA:** Casos de prueba E2E
- **Product Owners:** Validación de funcionalidades en UI

### Tecnologías
| Tecnología | Versión | Uso |
|---|---|---|
| Angular | 16+ | Framework principal |
| TypeScript | 5.0+ | Lenguaje tipado |
| RxJS | Latest | Programación reactiva |
| Angular Material | Latest | Componentes UI |
| Bootstrap 5 | 5.3+ | Grid y utilidades |
| Jasmine + Karma | Latest | Testing unitario |
| Cypress | Latest | Testing E2E |

### Componentes Principales
- `LoginComponent` - Autenticación
- `ProjectListComponent` - Listado de proyectos
- `RequirementListComponent` - Listado de requisitos
- `TraceabilityMatrixComponent` - Matriz de trazabilidad
- `HistoryViewerComponent` - Visualización de cambios
- `ExportErsComponent` - Exportación de documentos

---

## 🔗 4. MATRIZ DE TRAZABILIDAD GLOBAL
**Ubicación:** En cada documento de desarrollo

### Propósito
Mapear cada requisito del ERS (RF-001, RNF-001, etc.) a:
- ✅ Requisitos específicos del backend
- ✅ Requisitos específicos del frontend
- ✅ Endpoints REST
- ✅ Componentes y servicios
- ✅ Casos de prueba

### Ejemplo
```
Requisito ERS: RF-001 (Crear proyectos)
├── Backend: RFB-006 (POST /projects)
├── Frontend: RFF-016 (Modal de creación)
├── Endpoint: POST /api/projects
├── Componentes: ProjectCreateComponent
└── Test: ProjectService.spec.ts, ProjectCreateComponent.spec.ts
```

---

## 📊 Requisitos por Categoría

### Requisitos Funcionales Principales (ERS)

| ID | Requisito | Backend | Frontend | Prioridad |
|---|---|---|---|---|
| RF-001 | Crear proyectos | RFB-006 | RFF-016 | 🔴 ALTA |
| RF-002 | Registrar requisitos | RFB-013 | RFF-024 | 🔴 ALTA |
| RF-003 | Asignar tareas | RFB-022 | RFF-029 | 🟡 MEDIA |
| RF-004 | Gestionar cambios | RFB-027 | RFF-039 | 🔴 ALTA |
| RF-005 | Exportar ERS | RFB-032 | RFF-045 | 🔴 ALTA |

### Requisitos No Funcionales Principales

| Categoría | ERS | Backend | Frontend | Métrica |
|---|---|---|---|---|
| Rendimiento | RR-01 | RNFB-001 | RNFF-001 | < 2 segundos |
| Seguridad | AS-01 | RNFB-011 | RNFF-019 | JWT + HTTPS |
| Disponibilidad | RR-05 | RNFB-009 | RNFF-031 | 99% uptime |
| Usabilidad | OR-02 | N/A | RNFF-011 | Responsivo |
| Testing | N/A | RNFB-024 | RNFF-036 | >85% cobertura |

---

## 🚀 Flujo de Desarrollo Recomendado

### Fase 1: Planificación (Semana 1)

1. **Leer todos los documentos**
   - ERS completo
   - Backend requirements
   - Frontend requirements

2. **Validación de Requisitos**
   - [ ] ¿Todos los requisitos son claros?
   - [ ] ¿Hay ambigüedades?
   - [ ] ¿Se necesitan aclaraciones?

3. **Setup Inicial**
   - [ ] Crear repositorio Git
   - [ ] Configurar rama main y develop
   - [ ] Crear issues en GitHub/GitLab
   - [ ] Asignar tareas al equipo

### Fase 2: Desarrollo Backend (Semana 2-4)

1. **Setup Spring Boot** (~1 semana)
   - Crear proyecto Maven
   - Configurar dependencias
   - Estructura de carpetas
   - Configuración de base de datos

2. **Autenticación y Seguridad** (~3-4 días)
   - AuthService y AuthController
   - JWT configuration
   - SecurityConfig
   - Interceptores

3. **Core Functionality** (~1-2 semanas)
   - Proyectos (CRUD)
   - Requisitos (CRUD)
   - Trazabilidad
   - Historial de cambios

4. **Exportación y Testing** (~3-4 días)
   - Integración docx4j
   - Pruebas unitarias
   - Pruebas de integración

### Fase 3: Desarrollo Frontend (Semana 2-4)

1. **Setup Angular** (~2-3 días)
   - Crear proyecto Angular
   - Instalar Material/Bootstrap
   - Estructura modular
   - Configurar rutas

2. **Autenticación** (~3-4 días)
   - LoginComponent
   - AuthService
   - Guards y interceptores
   - Session management

3. **Interfaces** (~1-2 semanas)
   - Dashboard
   - Proyectos (CRUD UI)
   - Requisitos (CRUD UI)
   - Componentes auxiliares

4. **Funcionalidades Avanzadas** (~3-4 días)
   - Trazabilidad
   - Historial
   - Exportación
   - Estilos y responsividad

5. **Testing** (~3-4 días)
   - Pruebas unitarias
   - Pruebas E2E
   - Validación de usabilidad

### Fase 4: Integración y QA (Semana 5-6)

1. **Integración Backend + Frontend**
   - Verificar comunicación REST
   - CORS configuration
   - Manejo de errores

2. **Testing Completo**
   - Pruebas de seguridad
   - Pruebas de rendimiento
   - Pruebas de regresión

3. **Documentación**
   - API documentation (Swagger)
   - README del proyecto
   - Guía de instalación
   - Guía de usuario

4. **Deployment**
   - Configurar CI/CD
   - Build de producción
   - Deployment a entorno de staging

---

## 📋 Checklist Inicial (Pre-desarrollo)

### Documentación
- [ ] Todo el equipo ha leído el ERS completo
- [ ] Entendimiento común de requisitos
- [ ] Preguntas aclaradas con el cliente/stakeholders
- [ ] Requisitos priorizados

### Ambiente de Desarrollo
- [ ] Git configurado y repositorio creado
- [ ] Java 17+ instalado
- [ ] Node.js 18+ instalado
- [ ] IDE configurado (IntelliJ / VS Code)
- [ ] PostgreSQL/Supabase accesible
- [ ] Herramientas de testing preparadas

### Equipo
- [ ] Roles y responsabilidades asignados
- [ ] Líder técnico designado
- [ ] Schedule de reuniones definido
- [ ] Comunicación clara establecida
- [ ] Entendimiento de metodología XP

### Herramientas
- [ ] GitHub/GitLab con ramas configuradas
- [ ] Jira/Trello/GitHub Projects para tareas
- [ ] Slack/Teams para comunicación
- [ ] Documentación centralizada (Wiki)
- [ ] CI/CD pipeline básico

---

## 🔍 Guía por Rol

### Para Desarrollador Backend
1. Lee: `02_DESARROLLO_Backend_SpringBoot.md` (completo)
2. Referencias: `01_ERS_Requirement_Management.md` (secciones 3.1-3.5)
3. Enfócate en:
   - Requisitos RFB-001 a RFB-036
   - RNFB-001 a RNFB-028
   - Endpoints RESTful
   - Modelo de datos
   - Plan de testing

### Para Desarrollador Frontend
1. Lee: `03_DESARROLLO_Frontend_Angular.md` (completo)
2. Referencias: `01_ERS_Requirement_Management.md` (secciones 3.1-3.5)
3. Enfócate en:
   - Requisitos RFF-001 a RFF-055
   - RNFF-001 a RNFF-041
   - Componentes principales
   - Servicios
   - Plan de testing

### Para QA/Testing
1. Lee: `01_ERS_Requirement_Management.md` (secciones 3.2-3.6)
2. Lee: Secciones de testing en `02_DESARROLLO_Backend_SpringBoot.md` y `03_DESARROLLO_Frontend_Angular.md`
3. Crea casos de prueba basados en:
   - Matriz de trazabilidad
   - Criterios de aceptación
   - Plan de testing

### Para Project Manager
1. Lee: `01_ERS_Requirement_Management.md` (secciones 1.1-2.6)
2. Referencias: Secciones de "Matriz de Trazabilidad" en todos los documentos
3. Enfócate en:
   - Requisitos y alcance
   - Hitos y entregas
   - Matriz de trazabilidad
   - Aprobaciones

### Para Arquitecto/Tech Lead
1. Lee: Todos los documentos (completo)
2. Crea:
   - Diagrama de arquitectura
   - Diagrama ER de base de datos
   - Diagrama de componentes Angular
   - Documentación de decisiones arquitectónicas
3. Valida:
   - Consistencia entre documentos
   - Viabilidad técnica
   - Calidad del diseño

---

## 🔗 Referencias Cruzadas Rápidas

### Buscar un Requisito Específico

**RF-001: Crear proyectos**
- ERS: Sección 3.2.1
- Backend: RFB-006 en sección 3.2
- Frontend: RFF-015, RFF-016 en sección 3.3
- Endpoint: POST /api/projects
- Componente: ProjectCreateComponent

**RF-002: Registrar requisitos**
- ERS: Sección 3.2.2
- Backend: RFB-013 en sección 3.3
- Frontend: RFF-023, RFF-024 en sección 3.4
- Endpoint: POST /api/projects/{id}/requirements
- Componente: RequirementCreateComponent

**RF-004: Gestionar cambios**
- ERS: Sección 3.2.4
- Backend: RFB-027 a RFB-031 en sección 3.5
- Frontend: RFF-039 a RFF-044 en sección 3.6
- Tabla: requisitos_historial
- Componente: HistoryViewerComponent

---

## 📞 Preguntas Frecuentes (FAQ)

### Documentación

**P: ¿Cuál es la diferencia entre el ERS y los documentos de desarrollo?**
R: El ERS describe **QUÉ** debe hacer el sistema (requisitos de negocio). Los documentos de desarrollo describen **CÓMO** implementarlo (especificaciones técnicas).

**P: ¿Puedo modificar los requisitos una vez iniciado el desarrollo?**
R: Sí, pero debe registrarse formalmente como un cambio (Change Request) y evaluarse el impacto en cronograma y costo.

**P: ¿Todas las secciones del ERS son obligatorias?**
R: Las secciones 1, 2 y 3 son el núcleo. Las secciones 4-7 (aprobaciones, historial) se completan a lo largo del proyecto.

### Desarrollo

**P: ¿Qué hacer si un requisito es ambiguo?**
R: Documentar la ambigüedad, proponer una interpretación razonable, obtener aprobación escrita del cliente antes de implementar.

**P: ¿Qué base de datos debo usar?**
R: PostgreSQL via Supabase (especificado en restricciones RD-04). No uses SQLite en producción.

**P: ¿Es obligatorio usar Angular Material?**
R: No es obligatorio, pero está recomendado. Puedes usar Bootstrap 5 o componentes personalizados.

**P: ¿Cuál es el tiempo mínimo de respuesta de la API?**
R: < 500ms para endpoints normales (especificado en RNFB-001).

### Testing

**P: ¿Qué cobertura de tests es suficiente?**
R: Mínimo 85% en servicios y lógica core (RNFB-024), 60% en componentes (RNFF-036).

**P: ¿Debo hacer E2E antes de unitarias?**
R: No, el orden recomendado es: unitarias → integración → E2E.

---

## 📅 Timeline Recomendado (6 semanas)

```
SEMANA 1: Planificación
├─ Setup de environment
├─ Lectura y validación de requisitos
└─ Definición de arquitectura

SEMANA 2: Backend Auth + Frontend Base
├─ Backend: Autenticación y estructura
├─ Frontend: Setup, login, routing
└─ Testing unitario básico

SEMANA 3: Funcionalidades Core
├─ Backend: CRUD proyectos + requisitos
├─ Frontend: Interfaces de proyectos
└─ Testing integración

SEMANA 4: Funcionalidades Avanzadas
├─ Backend: Trazabilidad, historial, exportación
├─ Frontend: Trazabilidad, historial, exportación
└─ Testing E2E

SEMANA 5: Integración y QA
├─ Testing completo backend
├─ Testing completo frontend
├─ Pruebas de seguridad
└─ Optimización de rendimiento

SEMANA 6: Deployment y Documentación
├─ Setup de CI/CD
├─ Documentación final
├─ Capacitación de usuarios
└─ Deployment a producción
```

---

## 🎯 Criterios de Aceptación del Proyecto

El proyecto se considera **exitoso** cuando:

✅ **Funcionalidad**
- [ ] 100% de requisitos RF implementados y probados
- [ ] 100% de requisitos RNF validados
- [ ] Matriz de trazabilidad completa y verificada

✅ **Calidad**
- [ ] Cobertura de tests > 85% (backend), > 60% (frontend)
- [ ] 0 errores críticos en QA
- [ ] Documentación completa y actualizada

✅ **Rendimiento**
- [ ] Todos los endpoints responden < 500ms
- [ ] Dashboard renderiza en < 2 segundos
- [ ] Exportación ERS completa en < 5 segundos

✅ **Seguridad**
- [ ] JWT correctamente implementado
- [ ] HTTPS obligatorio
- [ ] Sin vulnerabilidades conocidas (OWASP Top 10)

✅ **Entrega**
- [ ] Código en repositorio con history limpio
- [ ] README con instrucciones de instalación
- [ ] Capacitación de usuarios completada
- [ ] Manual de usuario disponible

---

## 📞 Contacto y Soporte

| Rol | Responsable | Contacto |
|---|---|---|
| Arquitecto/Tech Lead | [Nombre] | [Email/Slack] |
| Desarrollador Backend | [Nombre] | [Email/Slack] |
| Desarrollador Frontend | [Nombre] | [Email/Slack] |
| QA Lead | [Nombre] | [Email/Slack] |
| Project Manager | [Nombre] | [Email/Slack] |

---

## 📝 Historial de Versiones del Índice

| Versión | Fecha | Cambios |
|---|---|---|
| 1.0 | [Escriba aquí] | Creación inicial del índice |
| 1.1 | [Escriba aquí] | [Describa cambios] |

---

**Índice y Guía de Navegación - Sistema REM**  
**Versión:** 1.0  
**Estado:** 🟢 Completo y Listo para Desarrollo  
**Última Actualización:** [Escriba aquí]

---

## 🔗 Acceso Rápido a Documentos

1. **Especificación de Requisitos (ERS)**
   → `01_ERS_Requirement_Management.md`

2. **Desarrollo Backend (Spring Boot)**
   → `02_DESARROLLO_Backend_SpringBoot.md`

3. **Desarrollo Frontend (Angular)**
   → `03_DESARROLLO_Frontend_Angular.md`

4. **Este Índice y Guía**
   → `00_INDICE_NAVEGACION.md`

---

**¡Listo para comenzar el desarrollo! 🚀**
