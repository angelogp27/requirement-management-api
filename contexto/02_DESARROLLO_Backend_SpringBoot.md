# Requisitos de Desarrollo - Backend
## Java Spring Boot | Requirement Management System (REM)

---

## 1. Introducción

Este documento especifica los requisitos técnicos y arquitectónicos para el desarrollo del **backend** del sistema Requirement Management (REM) utilizando **Java con Spring Boot**.

**Stack Tecnológico Recomendado:**
- **Lenguaje:** Java 17 o superior
- **Framework:** Spring Boot 3.1.x o superior
- **Gestor de Dependencias:** Maven o Gradle
- **Base de Datos:** PostgreSQL (via Supabase)
- **API:** RESTful con JSON
- **Autenticación:** JWT (JSON Web Tokens)
- **Testing:** JUnit 5, Mockito
- **CI/CD:** GitHub Actions o GitLab CI

---

## 2. Requisitos Arquitectónicos

### 2.1. Arquitectura General

| Aspecto | Especificación |
|---|---|
| **Patrón Arquitectónico** | Cliente-Servidor con arquitectura orientada a servicios (SOA) |
| **Comunicación** | API RESTful sobre protocolo HTTP/HTTPS |
| **Seguridad de Comunicación** | HTTPS obligatorio para todas las conexiones |
| **Estándar de Datos** | JSON para serialización de datos |
| **Autenticación** | JWT con tokens de corta duración |
| **Autorización** | Control de acceso basado en roles (RBAC) |

### 2.2. Estructura de Carpetas Recomendada

```
src/
├── main/
│   ├── java/com/rem/
│   │   ├── controller/        # Controladores REST
│   │   ├── service/           # Servicios de negocio
│   │   ├── repository/        # Acceso a datos (JPA)
│   │   ├── model/             # Entidades y DTOs
│   │   ├── config/            # Configuración de Spring
│   │   ├── security/          # Configuración de seguridad y JWT
│   │   ├── exception/         # Manejo de excepciones
│   │   └── utils/             # Utilidades generales
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       └── db/migration/      # Flyway/Liquibase
└── test/
    └── java/com/rem/         # Pruebas unitarias e integración
```

---

## 3. Requisitos Funcionales del Backend

### 3.1. Autenticación y Autorización

| Código | Requisito | Especificación |
|---|---|---|
| RFB-001 | Autenticación de Usuarios | Implementar endpoint POST /auth/login que valide credenciales y devuelva JWT token |
| RFB-002 | Generación de JWT | Token con expiración de 24 horas y refresh token con expiración de 7 días |
| RFB-003 | Control de Acceso | Implementar anotaciones @PreAuthorize para limitar acceso a endpoints según rol |
| RFB-004 | Cierre de Sesión | Endpoint POST /auth/logout que invalide tokens activos |
| RFB-005 | Validación de Permisos | Verificar permisos en cada endpoint según rol del usuario (ANALISTA, JEFE_PROYECTO, TECNICO) |

### 3.2. Gestión de Proyectos

| Código | Requisito | Especificación |
|---|---|---|
| RFB-006 | Crear Proyecto | POST /projects - Crear nuevo proyecto con campos: nombre, descripción, jefe_proyecto_id, analista_id |
| RFB-007 | Listar Proyectos | GET /projects - Retornar lista paginada de proyectos activos |
| RFB-008 | Obtener Proyecto | GET /projects/{id} - Obtener detalles de un proyecto específico |
| RFB-009 | Actualizar Proyecto | PUT /projects/{id} - Modificar datos del proyecto |
| RFB-010 | Eliminar Proyecto | DELETE /projects/{id} - Marcar proyecto como inactivo (soft delete) |
| RFB-011 | Generar Código Único | El sistema debe asignar automáticamente un código único (PRJ-001, PRJ-002, etc.) |
| RFB-012 | Validar Duplicidad | Verificar que el nombre del proyecto no exista en la base de datos antes de crear |

### 3.3. Gestión de Requisitos

| Código | Requisito | Especificación |
|---|---|---|
| RFB-013 | Crear Requisito | POST /projects/{projectId}/requirements - Registrar nuevo requisito con todos los atributos |
| RFB-014 | Listar Requisitos | GET /projects/{projectId}/requirements - Retornar lista paginada de requisitos del proyecto |
| RFB-015 | Filtrar Requisitos | GET /projects/{projectId}/requirements?estado=...&prioridad=... - Filtros por estado, prioridad, tipo |
| RFB-016 | Obtener Requisito | GET /projects/{projectId}/requirements/{id} - Detalles completos del requisito |
| RFB-017 | Actualizar Requisito | PUT /projects/{projectId}/requirements/{id} - Modificar cualquier atributo del requisito |
| RFB-018 | Eliminar Requisito | DELETE /projects/{projectId}/requirements/{id} - Soft delete (marcar como inactivo) |
| RFB-019 | Código Único por Proyecto | El sistema debe generar códigos únicos (RF-001, RNF-001, etc.) dentro de cada proyecto |
| RFB-020 | Validar Campos Obligatorios | Verificar que campos obligatorios estén completos: código, descripción, tipo, solicitante |
| RFB-021 | Historial de Versiones | Mantener registro completo de cambios con timestamp, usuario y valores anteriores |

### 3.4. Trazabilidad

| Código | Requisito | Especificación |
|---|---|---|
| RFB-022 | Asignar Requisito | POST /projects/{projectId}/requirements/{reqId}/assign - Asignar requisito a usuario técnico |
| RFB-023 | Obtener Matriz de Trazabilidad | GET /projects/{projectId}/traceability - Retornar matriz con requisito-usuario asignado |
| RFB-024 | Actualizar Asignación | PUT /projects/{projectId}/requirements/{reqId}/assign - Cambiar usuario asignado |
| RFB-025 | Historial de Asignaciones | Mantener registro de todas las asignaciones realizadas |
| RFB-026 | Validar Integridad | Verificar que los usuarios asignados existan y tengan rol técnico válido |

### 3.5. Gestión de Cambios

| Código | Requisito | Especificación |
|---|---|---|
| RFB-027 | Registrar Cambios | Capturar automáticamente cada modificación con fecha, hora, usuario y valores anteriores |
| RFB-028 | Obtener Historial | GET /projects/{projectId}/requirements/{reqId}/history - Retornar cronología de cambios |
| RFB-029 | Auditoría Inmutable | Los registros de auditoría NO deben ser modificables (insert-only en BD) |
| RFB-030 | Restaurar Versión | GET /projects/{projectId}/requirements/{reqId}/history/{versionId} - Obtener requisito en versión anterior |
| RFB-031 | Comparar Versiones | GET /projects/{projectId}/requirements/{reqId}/compare/{v1}/{v2} - Mostrar diferencias entre versiones |

### 3.6. Generación de Documentos

| Código | Requisito | Especificación |
|---|---|---|
| RFB-032 | Exportar ERS a DOCX | POST /projects/{projectId}/export/ers - Generar documento .docx con formato IEEE 830 |
| RFB-033 | Validar Estructura | Verificar que todos los requisitos del proyecto estén completos antes de exportar |
| RFB-034 | Plantilla IEEE 830 | Documento debe incluir: portada, índice, introducción, descripción general, requisitos específicos |
| RFB-035 | Tiempo de Exportación | Exportación debe completarse en máximo 5 segundos para 200 requisitos |
| RFB-036 | Formato Descargable | Retornar archivo .docx descargable con encabezado Content-Disposition |

---

## 4. Requisitos No Funcionales del Backend

### 4.1. Rendimiento

| Código | Requisito | Especificación |
|---|---|---|
| RNFB-001 | Tiempo Respuesta API | Todos los endpoints deben responder en máximo 500ms bajo carga normal |
| RNFB-002 | Conexión Base de Datos | Usar connection pooling (HikariCP) para optimizar conexiones |
| RNFB-003 | Paginación | Todas las listas deben estar paginadas (default 20 items, máximo 100) |
| RNFB-004 | Caché | Implementar caché en memoria para datos de proyectos que no cambian frecuentemente |
| RNFB-005 | Indexación BD | Crear índices en columnas frecuentemente consultadas (project_id, requirement_code, user_id) |

### 4.2. Confiabilidad y Disponibilidad

| Código | Requisito | Especificación |
|---|---|---|
| RNFB-006 | Manejo de Excepciones | Capturar y loguear todas las excepciones sin exponer detalles internos |
| RNFB-007 | Validación de Entrada | Validar todos los parámetros de entrada contra inyección SQL y XSS |
| RNFB-008 | Rollback de Transacciones | Revertir cambios automáticamente si una operación falla a mitad del proceso |
| RNFB-009 | Disponibilidad 99% | Sistema debe estar disponible 99% del tiempo durante horarios de trabajo |
| RNFB-010 | Recuperación de Errores | Implementar reintentos automáticos para operaciones transitorias |

### 4.3. Seguridad

| Código | Requisito | Especificación |
|---|---|---|
| RNFB-011 | Encriptación de Contraseñas | Usar BCrypt con salt para almacenar contraseñas |
| RNFB-012 | HTTPS Obligatorio | Todas las conexiones deben ser HTTPS en producción |
| RNFB-013 | CORS Configurado | Configurar CORS solo para dominio del frontend autorizado |
| RNFB-014 | Rate Limiting | Limitar requests a 100 por minuto por IP para prevenir ataques |
| RNFB-015 | Validación de Datos | Validar tipo, longitud y formato de todos los datos de entrada |
| RNFB-016 | Control de Acceso | Verificar permisos en cada endpoint; el usuario solo accede a sus datos |
| RNFB-017 | Tokens JWT Seguros | Usar algoritmo HS256 o RS256 con clave fuerte (mínimo 256 bits) |
| RNFB-018 | Auditoría de Seguridad | Loguear intentos de acceso no autorizados |

### 4.4. Mantenibilidad

| Código | Requisito | Especificación |
|---|---|---|
| RNFB-019 | Código Limpio | Seguir estándar Google Java Style Guide |
| RNFB-020 | Documentación de Código | Cada clase y método público debe tener Javadoc |
| RNFB-021 | Logging Estructurado | Usar SLF4J con patrones: [TIMESTAMP] [LEVEL] [CLASS] - Message |
| RNFB-022 | Control de Versiones | Usar Git con convención de commits: feat:, fix:, docs:, test:, refactor: |
| RNFB-023 | Arquitectura Modular | Separar por capas: controller, service, repository con responsabilidad única |

### 4.5. Testing

| Código | Requisito | Especificación |
|---|---|---|
| RNFB-024 | Cobertura Mínima | Alcanzar cobertura de código del 85% en clases de servicio y lógica core |
| RNFB-025 | Pruebas Unitarias | Cada clase de servicio debe tener pruebas unitarias con JUnit 5 y Mockito |
| RNFB-026 | Pruebas de Integración | Probar endpoints con @SpringBootTest e H2 en memoria |
| RNFB-027 | Pruebas de Seguridad | Verificar que endpoints no autorizados retornen 403/401 |
| RNFB-028 | Pruebas de Rendimiento | Validar que queries complejas responden en <500ms |

---

## 5. Endpoints RESTful Especificados

### 5.1. Autenticación

| Método | Endpoint | Parámetros | Respuesta |
|---|---|---|---|
| POST | /api/auth/login | username, password | { token, refreshToken, expiresIn } |
| POST | /api/auth/refresh | refreshToken | { token, expiresIn } |
| POST | /api/auth/logout | (Header: Authorization) | { message: "Logged out successfully" } |

### 5.2. Proyectos

| Método | Endpoint | Parámetros | Respuesta |
|---|---|---|---|
| POST | /api/projects | { nombre, descripcion, jefe_id, analista_id } | { id, codigo, nombre, ... } |
| GET | /api/projects?page=0&size=20 | Query: page, size | [ { id, codigo, nombre, ... } ] |
| GET | /api/projects/{id} | Path: projectId | { id, codigo, nombre, ... } |
| PUT | /api/projects/{id} | { nombre, descripcion, ... } | { id, codigo, nombre, ... } |
| DELETE | /api/projects/{id} | Path: projectId | { message: "Project deleted" } |

### 5.3. Requisitos

| Método | Endpoint | Parámetros | Respuesta |
|---|---|---|---|
| POST | /api/projects/{projectId}/requirements | { tipo, codigo, descripcion, solicitante, prioridad, costo } | { id, codigo, ... } |
| GET | /api/projects/{projectId}/requirements?estado=&prioridad=&page=0 | Query filters | [ { id, codigo, ... } ] |
| GET | /api/projects/{projectId}/requirements/{reqId} | Path: projectId, reqId | { id, codigo, descripcion, ... } |
| PUT | /api/projects/{projectId}/requirements/{reqId} | { descripcion, estado, prioridad, costo } | { id, codigo, ... } |
| DELETE | /api/projects/{projectId}/requirements/{reqId} | Path: projectId, reqId | { message: "Requirement deleted" } |

### 5.4. Trazabilidad

| Método | Endpoint | Parámetros | Respuesta |
|---|---|---|---|
| POST | /api/projects/{projectId}/requirements/{reqId}/assign | { user_id } | { requisito_id, usuario_id, fecha } |
| GET | /api/projects/{projectId}/traceability | Query: formato (json, csv) | Matriz de trazabilidad |
| GET | /api/projects/{projectId}/requirements/{reqId}/history | Query: page, size | [ { version, fecha, usuario, cambios } ] |
| GET | /api/projects/{projectId}/requirements/{reqId}/history/{versionId} | Path: versionId | Requisito en versión anterior |

### 5.5. Exportación

| Método | Endpoint | Parámetros | Respuesta |
|---|---|---|---|
| POST | /api/projects/{projectId}/export/ers | Query: format (docx) | .docx file (binary) |
| POST | /api/projects/{projectId}/export/csv | Query: includeHistory (true/false) | .csv file (text) |

---

## 6. Modelo de Datos

### 6.1. Entidades Principales

#### Tabla: usuarios

```sql
CREATE TABLE usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  rol ENUM('ANALISTA', 'JEFE_PROYECTO', 'TECNICO') NOT NULL,
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_ultima_conexion TIMESTAMP,
  CONSTRAINT chk_email_format CHECK (email LIKE '%@%.%')
);
```

#### Tabla: proyectos

```sql
CREATE TABLE proyectos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  codigo VARCHAR(20) UNIQUE NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  descripcion TEXT,
  jefe_proyecto_id BIGINT NOT NULL,
  analista_id BIGINT NOT NULL,
  estado ENUM('ACTIVO', 'PAUSADO', 'CERRADO') DEFAULT 'ACTIVO',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (jefe_proyecto_id) REFERENCES usuarios(id),
  FOREIGN KEY (analista_id) REFERENCES usuarios(id),
  INDEX idx_jefe (jefe_proyecto_id),
  INDEX idx_analista (analista_id)
);
```

#### Tabla: requisitos

```sql
CREATE TABLE requisitos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  proyecto_id BIGINT NOT NULL,
  codigo VARCHAR(50) NOT NULL,
  tipo ENUM('RF', 'RNF') NOT NULL,
  descripcion TEXT NOT NULL,
  solicitante_id BIGINT NOT NULL,
  precedencia VARCHAR(255),
  iteracion VARCHAR(50),
  estado ENUM('REGISTRADO', 'EN_ANALISIS', 'VALIDADO', 'APROBADO') DEFAULT 'REGISTRADO',
  prioridad ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') NOT NULL,
  costo_estimado DECIMAL(10, 2),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY unique_codigo_proyecto (proyecto_id, codigo),
  FOREIGN KEY (proyecto_id) REFERENCES proyectos(id),
  FOREIGN KEY (solicitante_id) REFERENCES usuarios(id),
  INDEX idx_proyecto (proyecto_id),
  INDEX idx_estado (estado),
  INDEX idx_prioridad (prioridad)
);
```

#### Tabla: requisitos_asignaciones

```sql
CREATE TABLE requisitos_asignaciones (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  requisito_id BIGINT NOT NULL,
  usuario_id BIGINT NOT NULL,
  fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_desasignacion TIMESTAMP NULL,
  UNIQUE KEY unique_asignacion (requisito_id, usuario_id),
  FOREIGN KEY (requisito_id) REFERENCES requisitos(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  INDEX idx_usuario (usuario_id)
);
```

#### Tabla: requisitos_historial

```sql
CREATE TABLE requisitos_historial (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  requisito_id BIGINT NOT NULL,
  usuario_id BIGINT NOT NULL,
  tipo_cambio ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
  campo_modificado VARCHAR(100),
  valor_anterior TEXT,
  valor_nuevo TEXT,
  descripcion_cambio TEXT,
  fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (requisito_id) REFERENCES requisitos(id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  INDEX idx_requisito (requisito_id),
  INDEX idx_fecha (fecha_cambio),
  INDEX idx_usuario (usuario_id)
);
```

---

## 7. Configuración de Spring Boot

### 7.1. Dependencias (pom.xml - Maven)

```xml
<dependencies>
  <!-- Spring Boot Starters -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>

  <!-- JWT -->
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
  </dependency>

  <!-- Base de Datos -->
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
  </dependency>

  <!-- Utilidades -->
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>

  <!-- Validación -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>

  <!-- Testing -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>

  <!-- Exportación a DOCX -->
  <dependency>
    <groupId>org.docx4j</groupId>
    <artifactId>docx4j</artifactId>
    <version>11.4.11</version>
  </dependency>
</dependencies>
```

### 7.2. application.properties

```properties
# Servidor
server.port=8080
server.servlet.context-path=/api

# Base de Datos
spring.datasource.url=jdbc:postgresql://[HOST]:[PORT]/[DATABASE]
spring.datasource.username=[USERNAME]
spring.datasource.password=[PASSWORD]
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL10Dialect

# JWT
app.jwt.secret=[GENERAR_CLAVE_FUERTE_MIN_256_BITS]
app.jwt.expiration=86400000
app.jwt.refreshExpiration=604800000

# Logging
logging.level.root=INFO
logging.level.com.rem=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# CORS
cors.allowed-origins=http://localhost:4200,https://ejemplo.com
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.max-age=3600
```

---

## 8. Plan de Testing

### 8.1. Pruebas Unitarias

| Componente | Casos de Prueba | Herramienta |
|---|---|---|
| AuthService | Login válido, credenciales inválidas, token expirado | JUnit 5 + Mockito |
| ProjectService | Crear proyecto, validar duplicidad, listar proyectos | JUnit 5 + Mockito |
| RequirementService | Crear requisito, validar campos, actualizar estado | JUnit 5 + Mockito |
| HistoryService | Registrar cambios, recuperar versiones, comparar | JUnit 5 + Mockito |

### 8.2. Pruebas de Integración

| Endpoint | Escenarios | Herramienta |
|---|---|---|
| POST /auth/login | Login exitoso, credenciales inválidas | @SpringBootTest + H2 |
| GET /projects | Listar proyectos, filtrar por estado | @SpringBootTest + H2 |
| POST /projects/{id}/requirements | Crear requisito válido, campos obligatorios faltantes | @SpringBootTest + H2 |
| DELETE /projects/{id} | Soft delete, validar inactividad | @SpringBootTest + H2 |

### 8.3. Pruebas de Seguridad

| Escenario | Validación | Respuesta Esperada |
|---|---|---|
| Acceso sin token | Request sin Authorization header | 401 Unauthorized |
| Token inválido | JWT malformado o expirado | 401 Unauthorized |
| Permiso insuficiente | Usuario sin rol JEFE_PROYECTO intenta eliminar proyecto | 403 Forbidden |
| Acceso a datos de otro usuario | Intentar acceder a proyecto de otro usuario | 403 Forbidden |

---

## 9. Checklist de Implementación del Backend

### Autenticación y Seguridad
- [ ] Implementar AuthController con endpoints login, refresh, logout
- [ ] Configurar SecurityConfig con @EnableWebSecurity
- [ ] Generar y almacenar JWT tokens de forma segura
- [ ] Implementar validación de roles con @PreAuthorize
- [ ] Encriptar contraseñas con BCrypt

### Gestión de Proyectos
- [ ] Crear entity Proyecto y repository
- [ ] Implementar ProjectController con CRUD
- [ ] Validar duplicidad de nombres
- [ ] Generar códigos únicos automáticamente
- [ ] Soft delete implementado

### Gestión de Requisitos
- [ ] Crear entity Requisito y repository
- [ ] Implementar RequirementController
- [ ] Validar campos obligatorios
- [ ] Generar códigos únicos por proyecto
- [ ] Implementar filtros (estado, prioridad, tipo)

### Trazabilidad
- [ ] Crear entity Asignación
- [ ] Implementar asignación de requisitos
- [ ] Generar matriz de trazabilidad
- [ ] Consultar asignaciones por usuario

### Historial y Auditoría
- [ ] Crear entity HistorialRequisito (tabla de auditoría)
- [ ] Interceptar cambios automáticamente
- [ ] Implementar versionado de requisitos
- [ ] Restaurar versiones anteriores

### Exportación
- [ ] Integrar librería docx4j
- [ ] Crear plantilla IEEE 830 en DOCX
- [ ] Implementar generación automática
- [ ] Validar completitud antes de exportar

### Testing
- [ ] Pruebas unitarias de servicios (cobertura > 85%)
- [ ] Pruebas de integración de endpoints
- [ ] Pruebas de seguridad
- [ ] Pruebas de rendimiento

### Documentación
- [ ] Javadoc en clases y métodos
- [ ] README con instrucciones de instalación
- [ ] Documentación de API (Swagger/SpringDoc)
- [ ] Guía de configuración de BD

---

## 10. Matriz de Trazabilidad - Requisitos ERS vs Desarrollo

| Requisito ERS | Requisito Backend | Endpoint | Status |
|---|---|---|---|
| RF-001: Crear proyectos | RFB-006 | POST /projects | [ ] Por implementar |
| RF-002: Registrar requisitos | RFB-013 | POST /projects/{id}/requirements | [ ] Por implementar |
| RF-003: Asignar tareas | RFB-022 | POST /projects/{id}/requirements/{id}/assign | [ ] Por implementar |
| RF-004: Gestionar cambios | RFB-027 | Captura automática en entity listeners | [ ] Por implementar |
| RF-005: Exportar ERS | RFB-032 | POST /projects/{id}/export/ers | [ ] Por implementar |

---

## 11. Recomendaciones Técnicas

### Optimizaciones
1. **Caché de Proyectos:** Implementar @Cacheable en métodos que obtienen proyectos frecuentemente consultados
2. **Paginación:** Usar PageRequest(0, 20) por defecto para listas grandes
3. **Lazy Loading:** Evitar cargar colecciones innecesarias con @LazyCollection
4. **Query Optimization:** Usar @EntityGraph para evitar N+1 queries

### Seguridad
1. **HTTPS en Producción:** Configurar certificados SSL/TLS válidos
2. **Rate Limiting:** Implementar con Spring Cloud Gateway o bucket4j
3. **CORS Restringido:** Whitelisting explícito de dominios autorizados
4. **Validación de Entrada:** Usar @Valid y custom validators

### Monitoreo y Logging
1. **Micrometer:** Integrar para métricas de aplicación
2. **ELK Stack:** Considerar para centralizar logs en producción
3. **Health Checks:** Implementar /actuator/health

---

**Documento preparado para desarrollo de Backend con Spring Boot**  
**Última actualización: [Escriba aquí]**  
**Versión: 1.0**
