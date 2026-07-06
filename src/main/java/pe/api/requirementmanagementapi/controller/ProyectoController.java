package pe.api.requirementmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.dto.request.ProyectoRequest;
import pe.api.requirementmanagementapi.dto.response.ApiResponse;
import pe.api.requirementmanagementapi.dto.response.ProyectoResponse;
import pe.api.requirementmanagementapi.dto.response.RequisitoResponse;
import pe.api.requirementmanagementapi.service.ProyectoService;
import pe.api.requirementmanagementapi.service.RequisitoService;
import pe.api.requirementmanagementapi.service.ErsExportService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de proyectos.
 * Endpoints según doc backend sección 5.2.
 * Base URL: /api/projects
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final RequisitoService requisitoService;
    private final ErsExportService ersExportService;

    /**
     * POST /api/projects - Crear un nuevo proyecto (RFB-006).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProyectoResponse>> crearProyecto(
            @Valid @RequestBody ProyectoRequest request) {
        ProyectoResponse response = proyectoService.crearProyecto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Proyecto creado exitosamente", response));
    }

    /**
     * GET /api/projects - Listar proyectos con paginación (RFB-007).
     * Parámetros: page (default 0), size (default 20).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProyectoResponse>>> listarProyectos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<ProyectoResponse> response = proyectoService.listarProyectos(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * GET /api/projects/{id} - Obtener proyecto por ID (RFB-008).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoResponse>> obtenerProyecto(@PathVariable UUID id) {
        ProyectoResponse response = proyectoService.obtenerProyecto(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * PUT /api/projects/{id} - Actualizar proyecto (RFB-009).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoResponse>> actualizarProyecto(
            @PathVariable UUID id,
            @Valid @RequestBody ProyectoRequest request) {
        ProyectoResponse response = proyectoService.actualizarProyecto(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Proyecto actualizado exitosamente", response));
    }

    /**
     * DELETE /api/projects/{id} - Soft delete (marca como CERRADO) (RFB-010).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarProyecto(@PathVariable UUID id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.ok(ApiResponse.ok("Proyecto eliminado exitosamente", null));
    }

    /**
     * GET /api/projects/{id}/traceability - Matriz de trazabilidad (RFB-023).
     * Retorna todos los requisitos del proyecto con sus asignaciones.
     */
    @GetMapping("/{id}/traceability")
    public ResponseEntity<ApiResponse<List<RequisitoResponse>>> obtenerTrazabilidad(
            @PathVariable UUID id) {
        List<RequisitoResponse> response = requisitoService.obtenerTrazabilidad(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * POST /api/projects/{id}/export/ers - Generar documento DOCX ERS (RFB-032).
     */
    @PostMapping("/{id}/export/ers")
    public ResponseEntity<byte[]> exportarERS(@PathVariable UUID id) {
        // Generamos un documento de ejemplo para la fase actual.
        // TODO: Implementar integración real con docx4j usando el servicio.
        String content = "Especificación de Requisitos de Software (ERS) - Proyecto " + id;
        byte[] docBytes = content.getBytes();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=REM_" + id + ".docx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        return new ResponseEntity<>(docBytes, headers, HttpStatus.OK);
    }

    /**
     * GET /api/projects/{id}/ers - Obtener el documento ERS Markdown.
     */
    @GetMapping("/{id}/ers")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> obtenerErs(
            @PathVariable UUID id) {
        var proyecto = proyectoService.obtenerProyectoEntity(id);
        String markdown = proyecto.getErsMarkdown();
        if (markdown == null) {
            markdown = generarPlantillaERS(proyecto.getNombre(), proyecto.getCodigo());
        } else if (markdown.startsWith("# ") || markdown.contains("## ")) {
            // Convert legacy markdown to HTML on the fly
            markdown = markdown.replace("\n", "<br>")
                    .replaceAll("# (.*?)(<br>|$)", "<h1>$1</h1>$2")
                    .replaceAll("## (.*?)(<br>|$)", "<h2>$1</h2>$2")
                    .replaceAll("### (.*?)(<br>|$)", "<h3>$1</h3>$2");
        } else if (markdown.contains("\\n")) {
            // sometimes it's literally \n escaped
            markdown = markdown.replace("\\n", "<br>");
        }
        
        // Cleanup any old generated stuff saved in the DB
        if (markdown.contains("--- REQUISITOS GENERADOS AUTOMÁTICAMENTE ---")) {
            int index = markdown.indexOf("--- REQUISITOS GENERADOS AUTOMÁTICAMENTE ---");
            int hrIndex = markdown.lastIndexOf("<hr>", index);
            if (hrIndex != -1 && hrIndex > index - 100) {
                index = hrIndex;
            }
            markdown = markdown.substring(0, index).trim();
        }
        
        String oldSection3 = "<h2>3. Requisitos Espec&iacute;ficos</h2>\n" +
                "<h3>3.1 Requisitos Funcionales</h3>\n" +
                "<p><strong>Los requisitos funcionales del proyecto se gestionan en el m&oacute;dulo de Captura.</strong></p>\n" +
                "<p>Puede insertar aqu&iacute; un resumen o referencia a los requisitos registrados.</p>\n" +
                "<h3>3.2 Requisitos No Funcionales</h3>\n" +
                "<p><strong>Los requisitos no funcionales se gestionan igualmente en el m&oacute;dulo de Captura.</strong></p>\n" +
                "<hr>\n";
        markdown = markdown.replace(oldSection3, "");
        // Append dynamic requirements preview
        String preview = ersExportService.generateErsMarkdownPreview(id);
        if (markdown.contains("<h2>4. Ap&eacute;ndices</h2>")) {
            markdown = markdown.replace("<h2>4. Ap&eacute;ndices</h2>", preview + "\n<h2>4. Ap&eacute;ndices</h2>");
        } else if (markdown.contains("<h2>4. Apéndices</h2>")) {
            markdown = markdown.replace("<h2>4. Apéndices</h2>", preview + "\n<h2>4. Apéndices</h2>");
        } else {
            markdown = markdown + preview;
        }
        
        return ResponseEntity.ok(ApiResponse.ok(java.util.Map.of("ersMarkdown", markdown)));
    }

    /**
     * PUT /api/projects/{id}/ers - Guardar el documento ERS Markdown.
     */
    @PutMapping("/{id}/ers")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> guardarErs(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body) {
        var proyecto = proyectoService.obtenerProyectoEntity(id);
        String incomingMarkdown = body.get("ersMarkdown");
        
        // Strip the dynamically generated part so we don't save it
        if (incomingMarkdown != null) {
            String splitMarkerStart = "<div id=\"auto-requirements-preview\">";
            String splitMarkerEnd = "</div><!-- END_AUTO_REQUIREMENTS -->";
            int startIndex = incomingMarkdown.indexOf(splitMarkerStart);
            if (startIndex != -1) {
                int endIndex = incomingMarkdown.indexOf(splitMarkerEnd, startIndex);
                if (endIndex != -1) {
                    incomingMarkdown = incomingMarkdown.substring(0, startIndex) + incomingMarkdown.substring(endIndex + splitMarkerEnd.length());
                } else {
                    incomingMarkdown = incomingMarkdown.substring(0, startIndex);
                }
            } else if (incomingMarkdown.contains("--- REQUISITOS GENERADOS AUTOMÁTICAMENTE ---")) {
                int index = incomingMarkdown.indexOf("<br><br><hr><br><div");
                if (index == -1) {
                    index = incomingMarkdown.indexOf("--- REQUISITOS GENERADOS AUTOMÁTICAMENTE ---");
                }
                if (index != -1) {
                    incomingMarkdown = incomingMarkdown.substring(0, index).trim();
                }
            }
        }
        
        proyecto.setErsMarkdown(incomingMarkdown);
        proyectoService.guardarProyectoEntity(proyecto);
        
        // Return with the preview appended so it stays in the editor
        // Return with the preview appended so it stays in the editor
        String preview = ersExportService.generateErsMarkdownPreview(id);
        String savedMarkdown = proyecto.getErsMarkdown();
        if (savedMarkdown != null && savedMarkdown.contains("<h2>4. Ap&eacute;ndices</h2>")) {
            savedMarkdown = savedMarkdown.replace("<h2>4. Ap&eacute;ndices</h2>", preview + "\n<h2>4. Ap&eacute;ndices</h2>");
        } else {
            savedMarkdown = (savedMarkdown == null ? "" : savedMarkdown) + preview;
        }
        
        return ResponseEntity.ok(ApiResponse.ok("ERS guardado exitosamente",
                java.util.Map.of("ersMarkdown", savedMarkdown)));
    }

    /**
     * Genera una plantilla ERS inicial basada en IEEE 830, en formato HTML.
     */
    private String generarPlantillaERS(String nombreProyecto, String codigoProyecto) {
        return """
<h1>Especificaci&oacute;n de Requisitos de Software (ERS)</h1>
<h2>Proyecto: %s (%s)</h2>
<hr>
<h2>1. Introducci&oacute;n</h2>
<h3>1.1 Prop&oacute;sito</h3>
<p><em>Describa el prop&oacute;sito de este documento ERS</em></p>
<h3>1.2 Alcance</h3>
<p><em>Describa el alcance del producto de software</em></p>
<h3>1.3 Definiciones, Acr&oacute;nimos y Abreviaturas</h3>
<p><em>Liste las definiciones importantes</em></p>
<h3>1.4 Referencias</h3>
<p><em>Liste los documentos de referencia</em></p>
<hr>
<h2>2. Descripci&oacute;n General</h2>
<h3>2.1 Perspectiva del Producto</h3>
<p><em>Describa el contexto del sistema</em></p>
<h3>2.2 Funciones del Producto</h3>
<p><em>Resuma las funciones principales</em></p>
<h3>2.3 Caracter&iacute;sticas de los Usuarios</h3>
<p><em>Describa los tipos de usuario</em></p>
<h3>2.4 Restricciones</h3>
<p><em>Liste las restricciones del sistema</em></p>
<h3>2.5 Suposiciones y Dependencias</h3>
<p><em>Liste las suposiciones</em></p>
<hr>
<h2>4. Ap&eacute;ndices</h2>
<p><em>Informaci&oacute;n adicional</em></p>
""".formatted(nombreProyecto, codigoProyecto);
    }
}
