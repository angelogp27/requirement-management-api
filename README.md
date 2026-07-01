# 🚀 API Backend - Sistema de Gestión de Requisitos (REM)

<div align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-3.2+-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Hibernate-6.4-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate">
</div>

## 📌 Visión General
Este es el backend oficial del Sistema de Gestión de Requisitos (REM). Se trata de una **API RESTful** robusta construida con **Spring Boot**, diseñada para soportar todo el ciclo de vida de la Ingeniería de Requisitos, siguiendo los principios de la norma IEEE 830.

La API se encarga de gestionar Proyectos, Requisitos, Trazabilidad, Usuarios (RBAC con JWT), y el Módulo de Control de Cambios (CCB).

---

## 🏗️ Arquitectura y Tecnologías

- **Framework:** Spring Boot 3.2+
- **Lenguaje:** Java 21+
- **Base de Datos:** PostgreSQL 16
- **ORM:** Hibernate / Spring Data JPA
- **Seguridad:** Spring Security con JWT (JSON Web Tokens)
- **Documentación:** Swagger / OpenAPI 3.0
- **Herramienta de Build:** Maven

---

## 🚀 Funcionalidades Principales Implementadas

### 1. Gestión de Proyectos y ERS
- CRUD de proyectos con asignación de Jefes de Proyecto y Analistas.
- Almacenamiento del documento de Especificación de Requisitos de Software (ERS) compatible con editores HTML (Quill) para exportación a DOCX desde el cliente.

### 2. Elicitación y Especificación (Requisitos)
- Gestión de Requisitos Funcionales (RF) y No Funcionales (RNF).
- Soporte para **Niveles de Ceremonia** (Alta y Baja Ceremonia) usando tipos dinámicos (`JSONB` en base de datos) para estructurar casos de uso con atributos como `flujoBasico`, `postcondiciones`, etc.
- Asignación de prioridades, estimación de costos y estados.

### 3. Trazabilidad y Stakeholders
- Relaciones cruzadas (Many-to-Many) entre Requisitos y Stakeholders (con distintos niveles de influencia).
- Dependencias (precedencia) entre múltiples requisitos.
- Matrices de trazabilidad para seguimiento.

### 4. Inteligencia Artificial: Red Bayesiana de Riesgos
- Módulo **CCB (Change Control Board)** para gestionar solicitudes de cambio sobre requisitos existentes.
- **`BayesianInferenceService`**: Motor de IA nativo (escrito 100% en Java sin librerías de ML externas) que calcula la probabilidad de riesgo de un cambio basado en un modelo **Naive Bayes**.
- **Variables Analizadas:** Prioridad del requisito, Nivel de Ceremonia y Cantidad de Stakeholders involucrados.
- **Resultado:** Puntuación normalizada persistida en BD (`probabilidad_riesgo`) para ayudar a la toma de decisiones.

### 5. Auditoría y Control (Checklists)
- Historial inmutable de cambios (trigger de base de datos) para rastrear quién hizo qué cambio.
- Soporte para evaluación de calidad de los requisitos (Checklist Peer Review) con banderas atómicas (es_testable, es_factible, etc.).

---

## 🛠️ Configuración y Ejecución

### 1. Base de Datos
- Crea una base de datos en PostgreSQL llamada `rem_db`.
- Los esquemas iniciales se crearán automáticamente gracias a Spring Boot (`schema.sql` y `data.sql`).

**Nota importante sobre los datos base (`data.sql`):**
El proyecto incluye un script de inicialización con **datos semilla masivos** muy completos, que inserta:
- Varios usuarios con diferentes roles (Jefes, Analistas, Técnicos).
- Más de 20 requisitos distribuidos en múltiples proyectos (E-commerce, RRHH, IA).
- Solicitudes de cambio pre-calculadas para ver la Red Bayesiana en acción.

### 2. Variables de Entorno / `application.yml`
Revisa el archivo de configuración para apuntar a tu base de datos:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/rem_db
    username: tu_usuario
    password: tu_password
  jpa:
    hibernate:
      ddl-auto: validate # Utiliza schema.sql para crear tablas
```

### 3. Iniciar el Servidor
Usando Maven:
```bash
mvn clean spring-boot:run
```
O directamente ejecutando la clase `RequirementManagementApiApplication` desde IntelliJ IDEA / Eclipse.

La API estará disponible por defecto en el puerto `8080`.

---

## 📚 Estructura del Código

```text
src/main/java/pe/api/requirementmanagementapi
 ├── config/       # Seguridad, JWT y CORS.
 ├── controller/   # Endpoints REST (Proyectos, Requisitos, Auth).
 ├── dto/          # Objetos de transferencia (Requests, Responses).
 ├── model/        # Entidades JPA (Requisito, ChangeRequest, etc).
 ├── repository/   # Interfaces Spring Data JPA.
 ├── security/     # Filtros y validaciones de tokens JWT.
 └── service/      # Lógica de negocio e inferencia bayesiana.
```

Hecho por el equipo de Ingeniería de Requisitos Avanzada.
