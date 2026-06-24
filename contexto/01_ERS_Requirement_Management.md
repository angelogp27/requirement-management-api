# Especificación de Requisitos de Software (ERS)
## Requirement Management System (REM)

---

## 1. Introducción

### 1.1. Propósito

El presente documento define de manera clara, precisa y verificable los requisitos del sistema **Requirement Management (REM)** siguiendo el estándar IEEE 830 para la elaboración de una Especificación de Requisitos de Software.

Este documento sirve como base para las actividades de análisis, diseño, implementación, validación y mantenimiento del sistema, estableciendo un entendimiento común entre usuarios, clientes y desarrolladores.

**Audiencia objetivo:**
- Analistas de requisitos
- Diseñadores y desarrolladores de software
- Equipo de pruebas y aseguramiento de calidad
- Jefe de proyecto

### 1.2. Ámbito del Sistema

El sistema **Requirement Management (REM)** tiene como finalidad gestionar de manera eficiente los requisitos de un proyecto de software, permitiendo registrar, organizar, clasificar, actualizar y dar seguimiento a los requerimientos funcionales y no funcionales durante todo el ciclo de vida del desarrollo.

#### Funcionalidades del Sistema

| Funcionalidad | Descripción |
|---|---|
| Registrar requisitos | Captura inicial de requisitos funcionales y no funcionales |
| Gestionar atributos | Prioridades, estados y trazabilidad de requisitos |
| Asociar elementos | Vinculación de requisitos con módulos, casos de uso y tareas |
| Historial de cambios | Mantener control de versiones y modificaciones |
| Validación y seguimiento | Facilitar aprobación formal de requisitos |
| Generación de reportes | Crear documentación relacionada con requerimientos |

#### Funcionalidades No Contempladas

El sistema **NO** incluye:
- Herramientas avanzadas de modelado UML
- Integración automática con plataformas externas de desarrollo
- Gestión financiera o contable del proyecto
- Gestión general de proyectos

#### Beneficios Esperados

- Mejor organización y control de requisitos
- Reducción de inconsistencias y ambigüedades
- Mayor facilidad para realizar seguimiento y mantenimiento
- Mejora en la comunicación entre clientes y desarrolladores
- Optimización del proceso de gestión de cambios en requisitos

### 1.3. Definiciones, Acrónimos y Abreviaturas

| Término | Definición |
|---|---|
| ERS | Especificación de Requisitos de Software |
| REM | Requirement Management |
| Requisito Funcional (RF) | Describe una función o comportamiento específico del sistema |
| Requisito No Funcional (RNF) | Describe restricciones o atributos de calidad del sistema |
| UML | Unified Modeling Language |
| Usuario | Persona que interactúa con el sistema |
| Trazabilidad | Capacidad de relacionar requisitos con otros elementos del sistema |
| API | Interfaz de Programación de Aplicaciones |
| HTTPS | Protocolo Seguro de Transferencia de Hipertexto |
| JWT | JSON Web Tokens |
| CORS | Cross-Origin Resource Sharing |
| IEEE 830 | Estándar IEEE para Especificación de Requisitos de Software |

### 1.4. Referencias

- IEEE Recommended Practice for Software Requirements Specifications (IEEE 830-1998)
- Pressman, Roger S. Ingeniería de Software: Un enfoque práctico
- Sommerville, Ian. Ingeniería de Software
- Documento base: "Especificación de Requisitos Software según el estándar IEEE 830"

### 1.5. Visión General del Documento

| Sección | Contenido |
|---|---|
| 1. Introducción | Propósito, alcance, definiciones y referencias del sistema |
| 2. Descripción General | Contexto del producto, funciones generales, usuarios y restricciones |
| 3. Requisitos Específicos | Requisitos funcionales, no funcionales, interfaces y restricciones |
| 4. Apéndices | Información complementaria relevante |
| 5. Índice | Facilita navegación y consulta |

---

## 2. Descripción General

En esta sección se describen los factores generales que afectan al sistema y a sus requisitos, estableciendo el contexto sin entrar en detalles técnicos profundos.

### 2.1. Perspectiva del Producto

El sistema REM es un producto de software **independiente y centralizado**. Surge como solución para reemplazar la gestión tradicional y descentralizada de requisitos (basada en documentos Word o matrices Excel dispersas) por un repositorio único e integrado.

El sistema actúa como plataforma principal para múltiples proyectos simultáneos, permitiendo que todo el equipo de desarrollo interactúe con una única fuente de verdad documental.

### 2.2. Funciones del Producto

Las funcionalidades principales del sistema REM están organizadas en torno a las **cuatro fases fundamentales de la ingeniería de requisitos**, soportadas por una actividad transversal:

| Fase | Descripción |
|---|---|
| Captura de Requisitos | Registro inicial de necesidades y requerimientos |
| Análisis de Requisitos | Evaluación, organización y refinamiento de información capturada |
| Especificación de Requisitos | Registro estructurado mediante atributos obligatorios |
| Validación de Requisitos | Aprobación formal para asegurar cumplimiento de expectativas |
| Gestión de Cambios (Transversal) | Actividad continua para evaluar impacto y mantener trazabilidad |
| Generación de Documentos | Creación automática de especificación formal (IEEE 830) |

### 2.3. Características de los Usuarios

| Perfil | Descripción |
|---|---|
| Analista de Requisitos | Usuario principal. Responsable directo de ejecutar las 4 fases de gestión documental |
| Jefe de Proyecto | Encargado de supervisión general. Visualiza estados, prioridades y costos |
| Diseñador / Programador / Probador | Usuarios técnicos que acceden de forma consultiva para especificaciones exactas |

### 2.4. Restricciones

| Tipo | Descripción |
|---|---|
| Diseño | Desarrollo con tecnologías web modernas: Angular (frontend) y Spring Boot (backend) |
| Operación | Software no es procesador de texto libre. Los usuarios ingresan datos parametrizados; el sistema ensamble el reporte final |

### 2.5. Suposiciones y Dependencias

**Suposiciones:**
- Los usuarios clave cuentan con formación teórica previa en ingeniería de requisitos
- Disponibilidad continua de entorno de red (internet/intranet)

**Dependencias:**
- Acceso a repositorio central
- Conectividad de red para acceder y generar reportes en tiempo real

### 2.6. Requisitos Futuros

- [ ] Integración con herramientas ágiles (Jira, Trello) para convertir requisitos en historias de usuario
- [ ] Asistencia con IA para detectar ambigüedades y redundancias
- [ ] Integración automática de APIs con plataformas externas

---

## 3. Requisitos Específicos

### 3.1. Interfaces Externas

#### 3.1.1. Interfaces de Usuario

La aplicación contará con una interfaz gráfica web adaptativa (responsive) que incluya:
- Pantallas de inicio de sesión
- Tablero de control (dashboard) para selección de proyectos activos
- Formularios estructurados para ingreso de requisitos
- Vistas tabulares para seguimiento de trazabilidad y estados

#### 3.1.2. Interfaces de Hardware

El sistema operará sobre servidores de aplicaciones web estándar y será accesible desde computadoras de escritorio o portátiles mediante navegadores comerciales. No requiere hardware periférico especializado.

#### 3.1.3. Interfaces de Software

El sistema interactuará con:
- Motor de base de datos relacional para almacenamiento centralizado
- Módulo de interfaz de salida para comunicarse con procesadores de texto
- Exportación de datos en formato .docx bajo estructura IEEE 830

#### 3.1.4. Interfaces de Comunicaciones

El intercambio de información entre navegador cliente y servidor se realizará bajo protocolo **HTTPS**, garantizando integridad de datos durante la navegación.

---

### 3.2. Funciones

#### 3.2.1. Gestión y Registro de Proyectos

| Atributo | Especificación |
|---|---|
| **Introducción** | El sistema debe permitir al usuario (Jefe de Proyecto) crear, modificar y eliminar proyectos como contenedor principal para todas las fases de gestión de requisitos |
| **Entradas** | Nombre del proyecto, descripción general, asignación de Jefe de Proyecto y Analista Responsable |
| **Proceso** | 1. Validar que el nombre no esté duplicado 2. Asignar código automático 3. Crear entorno del proyecto 4. Habilitar módulos de captura de requisitos |
| **Salidas** | Mensaje de confirmación y redirección al Dashboard del nuevo proyecto |

#### 3.2.2. Captura y Especificación Parametrizada de Requisitos

| Atributo | Especificación |
|---|---|
| **Introducción** | El sistema debe proporcionar un formulario estructurado para registrar detalladamente cada requerimiento, evitando texto libre |
| **Entradas** | Tipo de Requisito, Código, Descripción, Solicitante, Necesidad cubierta, Precedencia, Iteración/Sprint, Estado, Prioridad, Costo estimado |
| **Proceso** | 1. Capturar datos 2. Verificar campos obligatorios 3. Prevenir duplicidad de códigos 4. Clasificar y almacenar información |
| **Salidas** | Requisito guardado y visualizado como nueva fila en lista general |

#### 3.2.3. Trazabilidad y Asignación de Tareas

| Atributo | Especificación |
|---|---|
| **Introducción** | El sistema debe permitir vincular requisitos funcionales con miembros del equipo técnico para realizar seguimiento del desarrollo |
| **Entradas** | Selección de requisito previamente registrado y selección del usuario asignado (Programador, Diseñador o Probador) |
| **Proceso** | 1. Asociar requisito al perfil del desarrollador 2. Actualizar matriz de trazabilidad 3. Reflejar responsabilidad asignada |
| **Salidas** | Matriz de trazabilidad actualizada visible en tablero del proyecto |

#### 3.2.4. Gestión de Cambios y Control de Versiones

| Atributo | Especificación |
|---|---|
| **Introducción** | El sistema debe llevar control estricto de cualquier modificación realizada sobre un requisito como actividad transversal |
| **Entradas** | Nueva información ingresada en cualquier campo de requisito existente (ej. cambio de estado o modificación de costo) |
| **Proceso** | 1. Generar copia de versión anterior 2. Registrar automáticamente fecha, hora y autor 3. Crear historial inmutable |
| **Salidas** | Nuevo registro de auditoría visible en panel de "Historial de Cambios" |

#### 3.2.5. Generación Automática y Exportación del Documento ERS

| Atributo | Especificación |
|---|---|
| **Introducción** | El sistema debe automatizar la fase final compilando información ingresada para exportar documento formal de especificación |
| **Entradas** | Solicitud de exportación mediante botón (trigger) por Jefe de Proyecto o Analista |
| **Proceso** | 1. Realizar consulta a BD 2. Extraer requisitos del proyecto actual 3. Ensamblar información con plantilla IEEE 830 4. Maquetar documento |
| **Salidas** | Archivo descargable (.docx) con documento ERS estructurado y formateado |

---

### 3.3. Requisitos de Rendimiento

| Código | Requisito | Especificación |
|---|---|---|
| RR-01 | Tiempo de Respuesta de Interfaz | Renderizar vistas principales (Dashboard y formularios) en máximo 2 segundos bajo conectividad normal |
| RR-02 | Tiempo de Exportación | Procesar, ensamblar y permitir descarga de ERS en máximo 5 segundos (hasta 200 requisitos por proyecto) |
| RR-03 | Concurrencia de Usuarios | Soportar mínimo 50 sesiones simultáneas sin bloqueos de BD ni degradación >1 segundo |
| RR-04 | Capacidad de Almacenamiento | Gestionar 100 proyectos activos simultáneamente con promedio 500 registros por proyecto |
| RR-05 | Disponibilidad del Servicio | Garantizar 99% uptime durante horarios de trabajo del equipo de desarrollo |

---

### 3.4. Restricciones de Diseño

| Código | Restricción | Especificación |
|---|---|---|
| RD-01 | Arquitectura del Sistema | Arquitectura cliente-servidor orientada a servicios con API RESTful |
| RD-02 | Frontend | Interfaz de usuario construida obligatoriamente con Angular, diseño responsivo |
| RD-03 | Backend | Lógica de negocio implementada con Java y framework Spring Boot |
| RD-04 | Base de Datos | Sistema gestor BD relacional alojado en nube (Supabase o PlanetScale) |
| RD-05 | Formato de Exportación | Información maquetada usando exclusivamente estructura estándar IEEE 830-1998 |

---

### 3.5. Atributos del Sistema

| Código | Atributo | Especificación |
|---|---|---|
| AS-01 | Seguridad y Control de Acceso | Mecanismos de acceso restringido con contraseñas encriptadas y control de acceso basado en roles |
| AS-02 | Fiabilidad e Inmutabilidad | Garantizar que ningún registro sea eliminado sin dejar rastro auditable (historial de versiones y cambios) |
| AS-03 | Mantenibilidad | Código fuente estructurado con patrones de diseño reconocidos y alto nivel de documentación interna |

---

### 3.6. Otros Requisitos

| Código | Requisito | Especificación |
|---|---|---|
| OR-01 | Idioma de la Interfaz | Interfaz gráfica, mensajes de validación, notificaciones de error y documentos exportados en español |
| OR-02 | Compatibilidad de Navegadores | Funcional y correcta visualización en Chrome, Firefox, Edge y Safari (versiones estables recientes) |

---

## 4. Matriz de Trazabilidad de Requisitos

| RF/RNF | Descripción | Módulo | Prioridad | Estado |
|---|---|---|---|---|
| RF-001 | Crear proyectos | Gestión de Proyectos | Alta | [ ] Por definir |
| RF-002 | Registrar requisitos | Captura de Requisitos | Alta | [ ] Por definir |
| RF-003 | Asignar tareas | Trazabilidad | Media | [ ] Por definir |
| RF-004 | Gestionar cambios | Control de Versiones | Alta | [ ] Por definir |
| RF-005 | Exportar ERS | Generación de Documentos | Alta | [ ] Por definir |
| RNF-001 | Rendimiento de interfaz | Interfaces Externas | Media | [ ] Por definir |
| RNF-002 | Disponibilidad 99% | Atributos del Sistema | Alta | [ ] Por definir |
| RNF-003 | Seguridad y control de acceso | Atributos del Sistema | Crítica | [ ] Por definir |

---

## 5. Aprobaciones y Validaciones

### Información para Completar por el Cliente

| Aspecto | Estado | Observaciones |
|---|---|---|
| Alcance del proyecto aprobado | [ ] Sí / [ ] No | [Escriba aquí] |
| Requisitos funcionales validados | [ ] Sí / [ ] No | [Escriba aquí] |
| Restricciones técnicas aceptadas | [ ] Sí / [ ] No | [Escriba aquí] |
| Cronograma acordado | [ ] Sí / [ ] No | [Escriba aquí] |
| Presupuesto aprobado | [ ] Sí / [ ] No | [Escriba aquí] |

### Aprobaciones del Equipo de Desarrollo

| Rol | Nombre | Firma | Fecha |
|---|---|---|---|
| Líder Técnico | [Escriba aquí] | [ ] | [Escriba aquí] |
| Analista de Requisitos | [Escriba aquí] | [ ] | [Escriba aquí] |
| Jefe de Proyecto | [Escriba aquí] | [ ] | [Escriba aquí] |

---

## 6. Historial de Cambios

| Versión | Fecha | Autor | Cambios Realizados |
|---|---|---|---|
| 1.0 | [Escriba aquí] | [Escriba aquí] | Creación del documento inicial |
| 1.1 | [Escriba aquí] | [Escriba aquí] | [Escriba aquí] |
| 1.2 | [Escriba aquí] | [Escriba aquí] | [Escriba aquí] |

---

## 7. Índice de Secciones

1. Introducción
   - 1.1. Propósito
   - 1.2. Ámbito del Sistema
   - 1.3. Definiciones, Acrónimos y Abreviaturas
   - 1.4. Referencias
   - 1.5. Visión General del Documento

2. Descripción General
   - 2.1. Perspectiva del Producto
   - 2.2. Funciones del Producto
   - 2.3. Características de los Usuarios
   - 2.4. Restricciones
   - 2.5. Suposiciones y Dependencias
   - 2.6. Requisitos Futuros

3. Requisitos Específicos
   - 3.1. Interfaces Externas
   - 3.2. Funciones
   - 3.3. Requisitos de Rendimiento
   - 3.4. Restricciones de Diseño
   - 3.5. Atributos del Sistema
   - 3.6. Otros Requisitos

4. Matriz de Trazabilidad

5. Aprobaciones y Validaciones

6. Historial de Cambios

---

**Documento preparado según estándar IEEE 830-1998**  
**Última actualización: [Escriba aquí]**  
**Versión: 1.0**
