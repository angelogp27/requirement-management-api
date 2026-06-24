# Guía de Pruebas API - Sistema REM

## Archivo Postman

📄 **Importar en Postman:** `05_POSTMAN_COLLECTION.json`

> **File → Import → Upload Files → seleccionar `05_POSTMAN_COLLECTION.json`**

La colección contiene **27 pruebas** organizadas en 7 carpetas con tests automatizados y variables que se propagan automáticamente al ejecutar en orden secuencial.

---

## Configuración Previa

1. Crear la base de datos en PostgreSQL:
```sql
CREATE DATABASE rem_db;
```

2. Ejecutar el schema en la base de datos `rem_db`:
```sql
-- Copiar y ejecutar el contenido de:
-- src/main/resources/db/schema.sql
```

3. Iniciar la API:
```bash
mvn spring-boot:run
```

4. Base URL: `http://localhost:8080/api`

---

## Flujo de Pruebas Secuencial

Ejecutar las carpetas **en orden** (1 → 7). Los IDs se guardan automáticamente como variables de colección.

### 1. Usuarios (CRUD)

#### 1.1 Crear Jefe de Proyecto
```
POST http://localhost:8080/api/users
Content-Type: application/json

{
    "username": "jperez",
    "password": "123456",
    "email": "jperez@universidad.edu.pe",
    "rol": "JEFE_PROYECTO"
}
```
✅ Respuesta esperada: `201 Created` — Guardar el `id` retornado como `jefeId`.

#### 1.2 Crear Analista
```
POST http://localhost:8080/api/users
Content-Type: application/json

{
    "username": "mgarcia",
    "password": "123456",
    "email": "mgarcia@universidad.edu.pe",
    "rol": "ANALISTA"
}
```
✅ Respuesta esperada: `201 Created` — Guardar el `id` como `analistaId`.

#### 1.3 Crear Técnico
```
POST http://localhost:8080/api/users
Content-Type: application/json

{
    "username": "lrodriguez",
    "password": "123456",
    "email": "lrodriguez@universidad.edu.pe",
    "rol": "TECNICO"
}
```
✅ Respuesta esperada: `201 Created` — Guardar el `id` como `tecnicoId`.

#### 1.4 Listar Usuarios
```
GET http://localhost:8080/api/users
```
✅ Respuesta esperada: `200 OK` — Array con 3 usuarios.

#### 1.5 Obtener Usuario por ID
```
GET http://localhost:8080/api/users/{jefeId}
```
✅ Respuesta esperada: `200 OK` — No contiene campo `password`.

---

### 2. Proyectos (RFB-006 a RFB-012)

#### 2.1 Crear Proyecto
```
POST http://localhost:8080/api/projects
Content-Type: application/json

{
    "nombre": "Sistema de Gestión Académica",
    "descripcion": "Desarrollo del sistema de gestión académica para la facultad de ingeniería.",
    "jefeProyectoId": "{jefeId}",
    "analistaId": "{analistaId}"
}
```
✅ Respuesta esperada: `201 Created` — Código autogenerado `PRJ-001`, estado `ACTIVO`. Guardar `id` como `proyectoId`.

#### 2.2 Listar Proyectos (paginado)
```
GET http://localhost:8080/api/projects?page=0&size=20
```
✅ Respuesta esperada: `200 OK` — Objeto paginado con `content`, `totalElements`, `totalPages`.

#### 2.3 Error: Nombre Duplicado
```
POST http://localhost:8080/api/projects
Content-Type: application/json

{
    "nombre": "Sistema de Gestión Académica",
    "descripcion": "Intento de duplicado",
    "jefeProyectoId": "{jefeId}",
    "analistaId": "{analistaId}"
}
```
✅ Respuesta esperada: `409 Conflict` — Mensaje: "Proyecto ya existe con nombre: ..."

---

### 3. Requisitos (RFB-013 a RFB-021)

> ⚠️ **Header requerido:** `X-Usuario-Id: {analistaId}`

#### 3.1 Crear Requisito Funcional
```
POST http://localhost:8080/api/projects/{proyectoId}/requirements
Content-Type: application/json
X-Usuario-Id: {analistaId}

{
    "tipo": "RF",
    "descripcion": "El sistema debe permitir al usuario registrar una nueva matrícula ingresando código de alumno, ciclo académico y lista de cursos seleccionados.",
    "solicitanteId": "{jefeId}",
    "prioridad": "ALTA",
    "costoEstimado": 1500.00
}
```
✅ Respuesta: `201 Created` — Código autogenerado `RF-001`. Guardar `id` como `requisitoId`.

#### 3.2 Crear Segundo RF
```
POST http://localhost:8080/api/projects/{proyectoId}/requirements
Content-Type: application/json
X-Usuario-Id: {analistaId}

{
    "tipo": "RF",
    "descripcion": "El sistema debe generar un reporte de notas por alumno en formato PDF.",
    "solicitanteId": "{jefeId}",
    "prioridad": "MEDIA",
    "costoEstimado": 800.00
}
```
✅ Código autogenerado secuencial: `RF-002`. Guardar `id` como `requisitoId2`.

#### 3.3 Crear Requisito No Funcional
```
POST http://localhost:8080/api/projects/{proyectoId}/requirements
Content-Type: application/json
X-Usuario-Id: {analistaId}

{
    "tipo": "RNF",
    "descripcion": "El tiempo de respuesta no debe exceder los 2 segundos para cualquier consulta bajo carga de 50 usuarios.",
    "solicitanteId": "{jefeId}",
    "prioridad": "CRITICA",
    "costoEstimado": 2000.00
}
```
✅ Código autogenerado: `RNF-001` (numeración independiente por tipo).

#### 3.4 Listar con Filtros
```
GET http://localhost:8080/api/projects/{proyectoId}/requirements?tipo=RF
GET http://localhost:8080/api/projects/{proyectoId}/requirements?prioridad=CRITICA
GET http://localhost:8080/api/projects/{proyectoId}/requirements?estado=REGISTRADO&tipo=RF
```

#### 3.5 Actualizar Requisito (genera historial automático)
```
PUT http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId}
Content-Type: application/json
X-Usuario-Id: {analistaId}

{
    "tipo": "RF",
    "descripcion": "El sistema debe permitir al usuario registrar una nueva matrícula con modalidad de pago.",
    "solicitanteId": "{jefeId}",
    "prioridad": "CRITICA",
    "costoEstimado": 2500.00
}
```
✅ Los cambios en `prioridad` (ALTA→CRITICA), `costoEstimado` (1500→2500) y `descripcion` se registran automáticamente en el historial.

---

### 4. Estados y Asignación

#### 4.1 Cambiar Estado
```
PATCH http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId}/estado
Content-Type: application/json
X-Usuario-Id: {analistaId}

{ "estado": "EN_ANALISIS" }
```
Flujo completo: `REGISTRADO` → `EN_ANALISIS` → `VALIDADO` → `APROBADO`

#### 4.2 Asignar a Técnico
```
POST http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId}/assign
Content-Type: application/json
X-Usuario-Id: {analistaId}

{ "userId": "{tecnicoId}" }
```
✅ Solo usuarios con rol `TECNICO` pueden ser asignados.

#### 4.3 Error: Asignar a no-TECNICO
```
POST http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId}/assign
Content-Type: application/json
X-Usuario-Id: {analistaId}

{ "userId": "{analistaId}" }
```
✅ Respuesta: `400 Bad Request` — "Solo usuarios con rol TECNICO pueden ser asignados".

---

### 5. Historial y Dependencias

#### 5.1 Ver Historial de Cambios
```
GET http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId}/history?page=0&size=20
```
✅ Muestra todos los cambios: INSERT (creación), UPDATE (cada campo modificado), asignaciones, cambios de estado.

#### 5.2 Agregar Dependencia
```
POST http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId}/dependencias
Content-Type: application/json

{ "dependenciaId": "{requisitoId2}" }
```

#### 5.3 Ver Dependencias
```
GET http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId}/dependencias
```

---

### 6. Trazabilidad

#### 6.1 Matriz de Trazabilidad
```
GET http://localhost:8080/api/projects/{proyectoId}/traceability
```
✅ Retorna todos los requisitos del proyecto con su estado actual y usuario asignado.

---

### 7. Limpieza (Opcional)

#### 7.1 Eliminar Requisito
```
DELETE http://localhost:8080/api/projects/{proyectoId}/requirements/{requisitoId2}
X-Usuario-Id: {analistaId}
```

#### 7.2 Eliminar Proyecto (soft delete → CERRADO)
```
DELETE http://localhost:8080/api/projects/{proyectoId}
```

#### 7.3 Desactivar Usuario (soft delete → activo=false)
```
DELETE http://localhost:8080/api/users/{tecnicoId}
```

---

## Tabla Resumen de Endpoints

| # | Método | Endpoint | Descripción | Status |
|---|---|---|---|---|
| 1 | POST | `/api/users` | Crear usuario | 201 |
| 2 | GET | `/api/users` | Listar usuarios activos | 200 |
| 3 | GET | `/api/users/{id}` | Obtener usuario | 200 |
| 4 | PUT | `/api/users/{id}` | Actualizar usuario | 200 |
| 5 | DELETE | `/api/users/{id}` | Desactivar usuario | 200 |
| 6 | POST | `/api/projects` | Crear proyecto | 201 |
| 7 | GET | `/api/projects` | Listar proyectos | 200 |
| 8 | GET | `/api/projects/{id}` | Obtener proyecto | 200 |
| 9 | PUT | `/api/projects/{id}` | Actualizar proyecto | 200 |
| 10 | DELETE | `/api/projects/{id}` | Cerrar proyecto | 200 |
| 11 | GET | `/api/projects/{id}/traceability` | Matriz trazabilidad | 200 |
| 12 | POST | `/api/projects/{pId}/requirements` | Crear requisito | 201 |
| 13 | GET | `/api/projects/{pId}/requirements` | Listar requisitos | 200 |
| 14 | GET | `/api/projects/{pId}/requirements/{id}` | Obtener requisito | 200 |
| 15 | PUT | `/api/projects/{pId}/requirements/{id}` | Actualizar requisito | 200 |
| 16 | DELETE | `/api/projects/{pId}/requirements/{id}` | Eliminar requisito | 200 |
| 17 | PATCH | `/api/projects/{pId}/requirements/{id}/estado` | Cambiar estado | 200 |
| 18 | POST | `/api/projects/{pId}/requirements/{id}/assign` | Asignar técnico | 200 |
| 19 | GET | `/api/projects/{pId}/requirements/{id}/history` | Ver historial | 200 |
| 20 | POST | `/api/projects/{pId}/requirements/{id}/dependencias` | Agregar dependencia | 200 |
| 21 | GET | `/api/projects/{pId}/requirements/{id}/dependencias` | Ver dependencias | 200 |

---

## Códigos de Error

| Status | Causa | Ejemplo |
|---|---|---|
| 400 | Validación de campos / Regla de negocio | Campos vacíos, asignar a no-TECNICO |
| 404 | Recurso no encontrado | UUID inexistente |
| 409 | Duplicado | Username, email o nombre de proyecto repetido |
| 500 | Error interno | Error inesperado del servidor |
