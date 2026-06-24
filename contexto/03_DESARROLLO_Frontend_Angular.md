# Requisitos de Desarrollo - Frontend
## Angular | Requirement Management System (REM)

---

## 1. Introducción

Este documento especifica los requisitos técnicos y arquitectónicos para el desarrollo del **frontend** del sistema Requirement Management (REM) utilizando **Angular**.

**Stack Tecnológico Recomendado:**
- **Lenguaje:** TypeScript 5.0 o superior
- **Framework:** Angular 16 o superior (LTS)
- **Node.js:** 18 LTS o superior
- **Gestor de Paquetes:** npm 9+ o yarn 4+
- **Diseño de UI:** Bootstrap 5 / Angular Material
- **Estado:** RxJS / NgRx (opcional)
- **Testing:** Jasmine + Karma / Jest
- **E2E:** Cypress / Protractor
- **Linter:** ESLint + Prettier
- **Build:** Angular CLI

---

## 2. Requisitos Arquitectónicos

### 2.1. Arquitectura General

| Aspecto | Especificación |
|---|---|
| **Patrón de Aplicación** | Single Page Application (SPA) - totalmente reactiva |
| **Comunicación Backend** | HTTP REST mediante HttpClient con Observables (RxJS) |
| **Seguridad** | JWT almacenado en localStorage o sessionStorage |
| **Estándar de Datos** | JSON para serialización de datos |
| **Componentes** | Reutilizables, desacoplados y testeables |
| **Routing** | Lazy loading de módulos para optimización |
| **Responsividad** | Mobile-first design (breakpoints: xs, sm, md, lg, xl) |

### 2.2. Estructura de Carpetas Recomendada

```
src/
├── app/
│   ├── core/                        # Servicios singleton, guardianes, interceptores
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   ├── project.service.ts
│   │   │   ├── requirement.service.ts
│   │   │   └── api.service.ts
│   │   ├── guards/
│   │   │   ├── auth.guard.ts
│   │   │   └── role.guard.ts
│   │   ├── interceptors/
│   │   │   ├── auth.interceptor.ts
│   │   │   ├── error.interceptor.ts
│   │   └── models/
│   │       ├── user.model.ts
│   │       ├── project.model.ts
│   │       └── requirement.model.ts
│   ├── shared/                      # Componentes reutilizables
│   │   ├── components/
│   │   │   ├── navbar/
│   │   │   ├── sidebar/
│   │   │   ├── modal-dialog/
│   │   │   ├── button/
│   │   │   ├── form-field/
│   │   │   └── table/
│   │   ├── pipes/
│   │   │   ├── date.pipe.ts
│   │   │   └── priority.pipe.ts
│   │   ├── directives/
│   │   │   ├── autofocus.directive.ts
│   │   │   └── highlight.directive.ts
│   │   └── shared.module.ts
│   ├── features/                    # Módulos de funcionalidad
│   │   ├── auth/
│   │   │   ├── login/
│   │   │   ├── logout/
│   │   │   └── auth.module.ts
│   │   ├── projects/
│   │   │   ├── project-list/
│   │   │   ├── project-create/
│   │   │   ├── project-detail/
│   │   │   └── projects.module.ts
│   │   ├── requirements/
│   │   │   ├── requirement-list/
│   │   │   ├── requirement-create/
│   │   │   ├── requirement-edit/
│   │   │   ├── traceability-matrix/
│   │   │   ├── history-viewer/
│   │   │   └── requirements.module.ts
│   │   ├── dashboard/
│   │   │   ├── dashboard.component.ts
│   │   │   └── dashboard.module.ts
│   │   └── export/
│   │       ├── export-ers/
│   │       └── export.module.ts
│   ├── app.component.ts
│   ├── app.module.ts
│   ├── app-routing.module.ts
│   └── app.config.ts                # Constantes de configuración
├── assets/
│   ├── images/
│   ├── icons/
│   ├── styles/
│   │   ├── global.scss
│   │   ├── variables.scss
│   │   └── mixins.scss
│   └── data/
├── environments/
│   ├── environment.ts
│   ├── environment.dev.ts
│   ├── environment.prod.ts
├── styles/                          # Estilos globales
└── main.ts

tests/
├── unit/                           # Pruebas unitarias (.spec.ts)
└── e2e/                            # Pruebas end-to-end (Cypress)
```

---

## 3. Requisitos Funcionales del Frontend

### 3.1. Autenticación y Autorización

| Código | Requisito | Especificación |
|---|---|---|
| RFF-001 | Pantalla de Login | Formulario con campos username, password. Validación en tiempo real. Mostrar errores claros |
| RFF-002 | Almacenar Token JWT | Guardar token en localStorage con expiración controlada |
| RFF-003 | Refresh Token Automático | Solicitar nuevo token 30 segundos antes de expirar el actual |
| RFF-004 | Cerrar Sesión | Botón logout que limpie datos locales y redirija a login |
| RFF-005 | Proteger Rutas | Solo usuarios autenticados acceden a rutas protegidas (AuthGuard) |
| RFF-006 | Control de Roles | Mostrar/ocultar opciones según rol del usuario (ANALISTA, JEFE_PROYECTO, TECNICO) |
| RFF-007 | Mensaje de Sesión Expirada | Notificar cuando token expire y pedir re-login |
| RFF-008 | Recordar Usuario | Opción "Recordarme" para guardar username en localStorage |

### 3.2. Dashboard Principal

| Código | Requisito | Especificación |
|---|---|---|
| RFF-009 | Dashboard Responsivo | Pantalla principal con widgets de estado del sistema |
| RFF-010 | Proyectos Activos | Mostrar lista de proyectos del usuario actual |
| RFF-011 | Estadísticas | Gráficos con: total requisitos, por estado, por prioridad |
| RFF-012 | Acceso Rápido | Botones para crear proyecto, crear requisito, exportar documento |
| RFF-013 | Bienvenida Personalizada | Mostrar nombre del usuario en header/saludo |
| RFF-014 | Notificaciones | Indicador de nuevos cambios o asignaciones (opcional: con badge) |

### 3.3. Gestión de Proyectos

| Código | Requisito | Especificación |
|---|---|---|
| RFF-015 | Listar Proyectos | Tabla con paginación, búsqueda, filtros (estado, responsable) |
| RFF-016 | Crear Proyecto | Modal/formulario con campos: nombre, descripción, jefe, analista |
| RFF-017 | Editar Proyecto | Permitir modificar datos de proyecto (solo si usuario tiene permisos) |
| RFF-018 | Ver Detalles | Proyecto completo con lista de requisitos asociados |
| RFF-019 | Eliminar Proyecto | Soft delete con confirmación de diálogo |
| RFF-020 | Seleccionar Proyecto Activo | Cambiador de proyecto en navbar/sidebar |
| RFF-021 | Validación de Formulario | Campos requeridos marcados, mensajes de error en tiempo real |
| RFF-022 | Feedback Visual | Spinner de carga, mensajes toast de éxito/error |

### 3.4. Gestión de Requisitos

| Código | Requisito | Especificación |
|---|---|---|
| RFF-023 | Listar Requisitos | Tabla con paginación, búsqueda, filtros (tipo, estado, prioridad) |
| RFF-024 | Crear Requisito | Formulario extenso con validación y autocompletado |
| RFF-025 | Editar Requisito | Permitir modificar cualquier campo (solo si no está aprobado) |
| RFF-026 | Ver Detalles | Modal con información completa del requisito + historial |
| RFF-027 | Cambiar Estado | Dropdown para cambiar estado (REGISTRADO → EN_ANÁLISIS → VALIDADO → APROBADO) |
| RFF-028 | Cambiar Prioridad | Dropdown para actualizar prioridad en tiempo real |
| RFF-029 | Asignar a Usuario | Selector de usuario técnico para asignar requisito |
| RFF-030 | Eliminar Requisito | Hard delete con confirmación y registro en historial |
| RFF-031 | Validaciones Dinámicas | Campos requeridos según tipo de requisito |
| RFF-032 | Placeholder y Tooltips | Guías de ayuda para cada campo |

### 3.5. Trazabilidad

| Código | Requisito | Especificación |
|---|---|---|
| RFF-033 | Matriz de Trazabilidad | Tabla cruzada: Requisitos vs Usuarios Asignados |
| RFF-034 | Filtrar por Usuario | Ver solo requisitos asignados a un usuario |
| RFF-035 | Filtrar por Requisito | Ver quién está asignado a un requisito |
| RFF-036 | Exportar Matriz | Botón para descargar matriz en CSV |
| RFF-037 | Cambiar Asignación | Reasignar requisito a otro usuario desde la matriz |
| RFF-038 | Indicadores Visuales | Códigos de color para estados (verde=aprobado, rojo=bloqueado, etc.) |

### 3.6. Historial de Cambios

| Código | Requisito | Especificación |
|---|---|---|
| RFF-039 | Ver Historial | Pestaña con lista cronológica de cambios (más recientes primero) |
| RFF-040 | Detalles del Cambio | Mostrar: fecha, usuario, campo modificado, valor anterior, valor nuevo |
| RFF-041 | Restaurar Versión | Botón para revertir requisito a versión anterior |
| RFF-042 | Comparar Versiones | Visualización lado a lado de dos versiones |
| RFF-043 | Filtrar por Tipo | Mostrar solo INSERTs, UPDATEs o DELETEs |
| RFF-044 | Filtrar por Rango Dates | Seleccionar rango de fechas para ver cambios |

### 3.7. Exportación y Generación de Documentos

| Código | Requisito | Especificación |
|---|---|---|
| RFF-045 | Exportar ERS a DOCX | Botón que descarga documento formateado IEEE 830 |
| RFF-046 | Validación Previa | Mostrar advertencia si requisitos obligatorios faltan |
| RFF-047 | Spinner de Carga | Indicar progreso durante generación del documento |
| RFF-048 | Tamaño Máximo | Validar que proyecto no exceda límite de requisitos (200) |
| RFF-049 | Nombre de Archivo | Descargar con nombre: REM_[proyecto]_[fecha].docx |
| RFF-050 | Exportar a CSV | Botón alternativo para descargar requisitos en CSV |

### 3.8. Perfil de Usuario

| Código | Requisito | Especificación |
|---|---|---|
| RFF-051 | Ver Perfil | Opción en menú para ver/editar datos del usuario |
| RFF-052 | Cambiar Contraseña | Formulario seguro para actualizar password |
| RFF-053 | Preferencias | Seleccionar idioma, tema (claro/oscuro), cantidad de filas por página |
| RFF-054 | Última Conexión | Mostrar fecha/hora de último acceso |
| RFF-055 | Desactivar Cuenta | Opción para desactivar cuenta (solo usuarios) |

---

## 4. Requisitos No Funcionales del Frontend

### 4.1. Rendimiento

| Código | Requisito | Especificación |
|---|---|---|
| RNFF-001 | Carga Inicial | Tiempo para renderizar dashboard < 2 segundos |
| RNFF-002 | Lazy Loading | Módulos cargados bajo demanda (no cargar projects si estás en auth) |
| RNFF-003 | Paginación Eficiente | Cargar máximo 20 items por página |
| RNFF-004 | Búsqueda Instantánea | Filtros aplicados sin recargar la página (debounce 300ms) |
| RNFF-005 | Caché de Datos | Cachear proyectos del usuario durante sesión |
| RNFF-006 | Compresión de Recursos | Habilitar gzip para CSS, JS, HTML |
| RNFF-007 | Optimización de Imágenes | Usar WebP con fallback a PNG/JPG |
| RNFF-008 | Code Splitting | Dividir bundle en chunks por ruta |
| RNFF-009 | Tree Shaking | Eliminar código no utilizado en build de producción |
| RNFF-010 | Memory Leaks Prevention | Desuscribirse de Observables al destruir componentes |

### 4.2. Usabilidad e Interfaz

| Código | Requisito | Especificación |
|---|---|---|
| RNFF-011 | Diseño Responsivo | Funcional en: móvil (320px), tablet (768px), desktop (1920px) |
| RNFF-012 | Accesibilidad WCAG 2.1 AA | Navegación con teclado, etiquetas aria-label, contraste mínimo 4.5:1 |
| RNFF-013 | Feedback Visual | Spinners, animaciones suaves, transiciones claras |
| RNFF-014 | Notificaciones Toast | Mensajes de éxito/error en esquina inferior derecha |
| RNFF-015 | Confirmación de Acciones | Diálogos antes de eliminar o cambios críticos |
| RNFF-016 | Placeholder y Ayuda | Tooltips, hints en formularios |
| RNFF-017 | Indicadores de Estado | Colores estándar: verde=exitoso, rojo=error, amarillo=advertencia, azul=info |
| RNFF-018 | Interfaz Intuitiva | Botones principales destacados (color primario), acciones secundarias discretas |

### 4.3. Seguridad

| Código | Requisito | Especificación |
|---|---|---|
| RNFF-019 | Protección XSS | Usar property binding y event binding (no interpolación con innerHTML) |
| RNFF-020 | CSRF Protection | Incluir token CSRF en headers si backend lo requiere |
| RNFF-021 | Validación de Entrada | Sanitizar datos, validar longitud y formato |
| RNFF-022 | HTTPS Obligatorio | Solo permitir conexiones seguras |
| RNFF-023 | Storage Seguro | JWT no en localStorage si es altamente sensible (considerar sessionStorage) |
| RNFF-024 | Logout Automático | Limpiar datos al cerrar sesión o expirar token |
| RNFF-025 | Headers Seguros | Content-Security-Policy, X-Content-Type-Options, X-Frame-Options |
| RNFF-026 | Validación de Rol | Verificar roles en frontend Y backend |

### 4.4. Confiabilidad

| Código | Requisito | Especificación |
|---|---|---|
| RNFF-027 | Manejo de Errores | Capturar excepciones HTTP (4xx, 5xx) y mostrar mensaje amigable |
| RNFF-028 | Reintentos Automáticos | Para peticiones fallidas por timeout o conexión inestable |
| RNFF-029 | Fallback de Datos | Si caché está disponible, mostrar datos viejos mientras se actualiza |
| RNFF-030 | Sincronización | Si hay cambios simultáneos, refrescar datos automáticamente |
| RNFF-031 | Validación de Estado | Verificar que datos en componentes sean consistentes |

### 4.5. Compatibilidad

| Código | Requisito | Especificación |
|---|---|---|
| RNFF-032 | Navegadores Soportados | Chrome 90+, Firefox 88+, Edge 90+, Safari 14+ |
| RNFF-033 | Resoluciones | Mínimo 320px (móvil), máximo 1920px+ (desktop) |
| RNFF-034 | Velocidad de Conexión | Funcional en 3G (velocidad mínima: 1Mbps descarga) |
| RNFF-035 | Dispositivos Táctiles | Botones con tamaño mínimo de 44x44px para tocar cómodamente |

### 4.6. Mantenibilidad

| Código | Requisito | Especificación |
|---|---|---|
| RNFF-036 | Código Limpio | Seguir Angular Style Guide y Google TypeScript Guide |
| RNFF-037 | Componentes Reutilizables | Máximo una responsabilidad por componente |
| RNFF-038 | Tipado Fuerte | Usar TypeScript strictamente (no usar `any`) |
| RNFF-039 | Documentación | Comentarios en métodos complejos, README en cada módulo |
| RNFF-040 | Testing Mínimo | Cobertura > 80% en servicios, > 60% en componentes |
| RNFF-041 | Versionado | Control de versiones con Git, mensajes de commit claros |

---

## 5. Componentes Principales Especificados

### 5.1. Componente de Login

```typescript
// login.component.ts
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  rememberMe = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  onSubmit() {
    // Validar formulario
    // Llamar authService.login()
    // Redirigir a dashboard
  }
}
```

**HTML:**
```html
<div class="login-container">
  <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
    <h1>Sistema de Gestión de Requisitos</h1>
    <mat-form-field>
      <mat-label>Usuario</mat-label>
      <input matInput formControlName="username" required>
    </mat-form-field>
    <mat-form-field>
      <mat-label>Contraseña</mat-label>
      <input matInput type="password" formControlName="password" required>
    </mat-form-field>
    <mat-checkbox formControlName="rememberMe">Recordarme</mat-checkbox>
    <button mat-raised-button color="primary" [disabled]="isLoading">
      <mat-spinner *ngIf="isLoading" diameter="20"></mat-spinner>
      Iniciar Sesión
    </button>
  </form>
</div>
```

### 5.2. Componente de Lista de Proyectos

```typescript
// project-list.component.ts
@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html'
})
export class ProjectListComponent implements OnInit {
  projects$: Observable<Project[]>;
  displayedColumns: string[] = ['codigo', 'nombre', 'jefe', 'estado', 'acciones'];
  filtros = new FormGroup({
    search: new FormControl(''),
    estado: new FormControl('')
  });

  constructor(private projectService: ProjectService) {}

  ngOnInit() {
    this.projects$ = this.filtros.valueChanges.pipe(
      startWith(null),
      switchMap(() => this.projectService.getProjects())
    );
  }

  crearProyecto() { /* Modal para crear */ }
  editarProyecto(id: string) { /* Modal para editar */ }
  eliminarProyecto(id: string) { /* Confirmación y soft delete */ }
}
```

### 5.3. Componente de Tabla de Requisitos

```typescript
// requirement-list.component.ts
@Component({
  selector: 'app-requirement-list',
  templateUrl: './requirement-list.component.html'
})
export class RequirementListComponent implements OnInit {
  requirements$: Observable<Requirement[]>;
  displayedColumns: string[] = ['codigo', 'descripcion', 'tipo', 'estado', 'prioridad', 'asignado', 'acciones'];
  pageSize = 20;
  paginator: PageEvent;

  constructor(
    private requirementService: RequirementService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const projectId = this.route.snapshot.paramMap.get('projectId');
    this.requirements$ = this.requirementService.getRequirements(projectId);
  }

  cambiarEstado(requirementId: string, nuevoEstado: string) { /* Actualizar */ }
  asignarUsuario(requirementId: string, userId: string) { /* Asignar */ }
  verHistorial(requirementId: string) { /* Abrir modal */ }
}
```

### 5.4. Componente de Matriz de Trazabilidad

```typescript
// traceability-matrix.component.ts
@Component({
  selector: 'app-traceability-matrix',
  templateUrl: './traceability-matrix.component.html'
})
export class TraceabilityMatrixComponent implements OnInit {
  matrixData$: Observable<TraceabilityMatrix[]>;
  requirements$: Observable<Requirement[]>;
  users$: Observable<User[]>;

  constructor(private projectService: ProjectService) {}

  ngOnInit() {
    const projectId = this.route.snapshot.paramMap.get('projectId');
    this.matrixData$ = this.projectService.getTraceabilityMatrix(projectId);
  }

  exportarCSV() { /* Descargar matriz */ }
  reasignar(reqId: string, userId: string) { /* Cambiar asignación */ }
}
```

---

## 6. Servicios Principales

### 6.1. AuthService

```typescript
// auth.service.ts
@Injectable({ providedIn: 'root' })
export class AuthService {
  private token$ = new BehaviorSubject<string | null>(null);
  private user$ = new BehaviorSubject<User | null>(null);

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/auth/login', { username, password })
      .pipe(
        tap(response => {
          this.token$.next(response.token);
          localStorage.setItem('token', response.token);
          this.setRefreshTimer(response.expiresIn);
        })
      );
  }

  logout(): Observable<void> {
    return this.http.post<void>('/auth/logout', {})
      .pipe(
        finalize(() => {
          this.token$.next(null);
          this.user$.next(null);
          localStorage.removeItem('token');
        })
      );
  }

  refreshToken(): Observable<AuthResponse> { /* ... */ }
  getUser(): Observable<User> { /* ... */ }
  isAuthenticated(): boolean { /* ... */ }
  hasRole(role: string): boolean { /* ... */ }
}
```

### 6.2. ProjectService

```typescript
// project.service.ts
@Injectable({ providedIn: 'root' })
export class ProjectService {
  private apiUrl = '/api/projects';

  constructor(private http: HttpClient) {}

  getProjects(): Observable<ApiResponse<Page<Project>>> {
    return this.http.get<ApiResponse<Page<Project>>>(this.apiUrl)
      .pipe(
        shareReplay(1),  // Cachear resultado
        catchError(error => this.handleError(error))
      );
  }

  createProject(project: Partial<Project>): Observable<ApiResponse<Project>> {
    return this.http.post<ApiResponse<Project>>(this.apiUrl, project);
  }

  updateProject(id: string, project: Partial<Project>): Observable<ApiResponse<Project>> {
    return this.http.put<ApiResponse<Project>>(`${this.apiUrl}/${id}`, project);
  }

  deleteProject(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  getTraceabilityMatrix(projectId: string): Observable<ApiResponse<TraceabilityMatrix[]>> {
    return this.http.get<ApiResponse<TraceabilityMatrix[]>>(
      `${this.apiUrl}/${projectId}/traceability`
    );
  }

  exportERS(projectId: string): Observable<Blob> {
    return this.http.post(
      `${this.apiUrl}/${projectId}/export/ers`,
      {},
      { responseType: 'blob' }
    ).pipe(
      tap(blob => this.downloadFile(blob, `REM_${projectId}.docx`))
    );
  }

  private handleError(error: HttpErrorResponse) { /* ... */ }
}
```

---

## 7. Configuración de Interceptores

### 7.1. AuthInterceptor

```typescript
// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    
    if (token) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(request);
  }
}
```

### 7.2. ErrorInterceptor

```typescript
// error.interceptor.ts
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router, private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError(error => {
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        if (error.status === 403) {
          console.error('Acceso denegado');
        }
        return throwError(() => error);
      })
    );
  }
}
```

---

## 8. Rutas y Módulos

### 8.1. AppRoutingModule

```typescript
// app-routing.module.ts
const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'projects',
    canActivate: [AuthGuard],
    children: [
      { path: '', component: ProjectListComponent },
      { path: 'new', component: ProjectCreateComponent },
      { path: ':projectId', component: ProjectDetailComponent },
      {
        path: ':projectId/requirements',
        loadChildren: () => import('./features/requirements/requirements.module')
          .then(m => m.RequirementsModule)
      }
    ]
  },
  { path: '**', component: PageNotFoundComponent }
];
```

---

## 9. Modelos y DTOs

### 9.1. Interfaces Base

```typescript
// api-response.model.ts
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
```

### 9.2. Modelos Generales

```typescript
// user.model.ts
export interface User {
  id: string;
  username: string;
  email: string;
  rol: 'ANALISTA' | 'JEFE_PROYECTO' | 'TECNICO';
  activo: boolean;
}

// project.model.ts
export interface Project {
  id: string;
  codigo: string;
  nombre: string;
  descripcion: string;
  jefeProyecto: User;
  analista: User;
  estado: 'ACTIVO' | 'PAUSADO' | 'CERRADO';
  fechaCreacion: Date;
}

// requirement.model.ts
export interface Requirement {
  id: string;
  codigo: string;
  tipo: 'RF' | 'RNF';
  descripcion: string;
  solicitante: User;
  estado: 'REGISTRADO' | 'EN_ANALISIS' | 'VALIDADO' | 'APROBADO';
  prioridad: 'BAJA' | 'MEDIA' | 'ALTA' | 'CRITICA';
  costoEstimado: number;
  asignadoA?: User;
  fechaCreacion: Date;
}
```

---

## 10. Estilos y Diseño

### 10.1. Paleta de Colores

| Color | Valor | Uso |
|---|---|---|
| Primario | #2196F3 | Botones principales, links activos |
| Éxito | #4CAF50 | Estados positivos, validaciones |
| Alerta | #FF9800 | Advertencias, estados en progreso |
| Error | #F44336 | Errores, estados críticos |
| Neutral | #9E9E9E | Textos secundarios, deshabilitado |
| Fondo | #FAFAFA | Fondo de la aplicación |

### 10.2. Tipografía

```scss
// variables.scss
$font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
$font-size-base: 14px;
$font-size-lg: 18px;
$font-size-xl: 24px;
$font-weight-normal: 400;
$font-weight-bold: 600;

h1 { font-size: 2rem; font-weight: 600; margin-bottom: 1rem; }
h2 { font-size: 1.5rem; font-weight: 600; margin-bottom: 0.8rem; }
h3 { font-size: 1.2rem; font-weight: 600; margin-bottom: 0.6rem; }
```

---

## 11. Plan de Testing

### 11.1. Testing Unitario

| Componente | Casos de Prueba | Herramienta |
|---|---|---|
| LoginComponent | Validación de form, submit exitoso, error de credenciales | Jasmine + Karma |
| ProjectListComponent | Listar proyectos, paginación, filtros, crear/editar/eliminar | Jasmine + Karma |
| RequirementListComponent | Listar requisitos, cambiar estado, asignar, historial | Jasmine + Karma |
| AuthService | Login, logout, refresh, isAuthenticated, hasRole | Jasmine + Mockito |
| ProjectService | CRUD de proyectos, matriz de trazabilidad, exportación | Jasmine + HttpClientTestingModule |

### 11.2. Testing E2E

| Flujo | Casos de Prueba | Herramienta |
|---|---|---|
| Login | Credenciales válidas, inválidas, sesión expirada | Cypress |
| Crear Proyecto | Llenar formulario, validar duplicidad, éxito | Cypress |
| Crear Requisito | Llenar formulario, validar campos, guardado | Cypress |
| Exportar ERS | Verificar descarga, contenido válido | Cypress |

---

## 12. Checklist de Implementación del Frontend

### Setup Inicial
- [ ] Crear proyecto Angular con CLI
- [ ] Instalar dependencias (Material, Bootstrap, RxJS)
- [ ] Configurar rutas y módulos lazy loading
- [ ] Configurar HttpClient e interceptores

### Autenticación
- [ ] Componente de login
- [ ] AuthService con JWT
- [ ] AuthGuard y RoleGuard
- [ ] Refresh token automático
- [ ] Logout

### Interfaces
- [ ] Dashboard con estadísticas
- [ ] Lista y formularios de proyectos
- [ ] Lista y formularios de requisitos
- [ ] Matriz de trazabilidad
- [ ] Visor de historial

### Funcionalidades
- [ ] CRUD de proyectos
- [ ] CRUD de requisitos
- [ ] Asignación de requisitos
- [ ] Cambios de estado
- [ ] Exportación ERS

### Validaciones
- [ ] Validación de formularios (cliente)
- [ ] Manejo de errores HTTP
- [ ] Mensajes de notificación
- [ ] Confirmaciones de acciones críticas

### Estilos
- [ ] Tema personalizado (colores, tipografía)
- [ ] Responsividad completa
- [ ] Animaciones suaves
- [ ] Iconografía consistente

### Testing
- [ ] Pruebas unitarias (cobertura > 80%)
- [ ] Pruebas E2E para flujos críticos
- [ ] Pruebas de seguridad

### Optimizaciones
- [ ] Code splitting y lazy loading
- [ ] Caché de datos
- [ ] Compression de assets
- [ ] Production build

---

## 13. Matriz de Trazabilidad - Requisitos ERS vs Frontend

| Requisito ERS | Requisito Frontend | Componente | Status |
|---|---|---|---|
| RF-001: Crear proyectos | RFF-016 | ProjectCreateComponent | [ ] Por implementar |
| RF-002: Registrar requisitos | RFF-024 | RequirementCreateComponent | [ ] Por implementar |
| RF-003: Asignar tareas | RFF-029 | RequirementListComponent | [ ] Por implementar |
| RF-004: Gestionar cambios | RFF-039 | HistoryViewerComponent | [ ] Por implementar |
| RF-005: Exportar ERS | RFF-045 | ExportErsComponent | [ ] Por implementar |

---

## 14. Recomendaciones Técnicas

### Optimizaciones de Rendimiento
1. **Lazy Loading de Módulos:** No cargar feature modules hasta que se navegue a ellos
2. **OnPush Change Detection:** Usar en componentes sin lógica compleja
3. **Virtual Scrolling:** Para listas muy largas (requiere CDK)
4. **Image Lazy Loading:** Cargar imágenes bajo demanda

### Buenas Prácticas
1. **Desuscribirse:** Usar `async` pipe o `takeUntil` para evitar memory leaks
2. **Smart & Dumb Components:** Separar lógica (container) de presentación (presentational)
3. **Reactive Forms:** Usar FormGroup/FormControl en lugar de ngModel
4. **Observables:** Preferir Observables sobre Promises para RxJS

### Seguridad
1. **Sanitización:** Usar DomSanitizer para contenido dinámico
2. **Content Security Policy:** Configurar headers CSP
3. **No localStorage sensible:** Para datos críticos, usar sessionStorage o memory

---

**Documento preparado para desarrollo de Frontend con Angular**  
**Última actualización: [Escriba aquí]**  
**Versión: 1.0**
