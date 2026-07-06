package pe.api.requirementmanagementapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.service.ErsExportService;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/export-ers")
@RequiredArgsConstructor
public class ErsController {

    private final ErsExportService ersExportService;

    @GetMapping
    public ResponseEntity<byte[]> exportErsDocument(@PathVariable UUID projectId) {
        try {
            ClassPathResource templateResource = new ClassPathResource("templates/ers_template.docx");
            InputStream templateStream;

            if (!templateResource.exists()) {
                // Generate a dummy template on the fly if it doesn't exist
                // Generate a MODERN dummy template on the fly
                org.apache.poi.xwpf.usermodel.XWPFDocument dummyDoc = new org.apache.poi.xwpf.usermodel.XWPFDocument();
                
                // Título Principal
                org.apache.poi.xwpf.usermodel.XWPFParagraph pTitle = dummyDoc.createParagraph();
                pTitle.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                org.apache.poi.xwpf.usermodel.XWPFRun rTitle = pTitle.createRun();
                rTitle.setText("Documento de Especificación de Requisitos (ERS)");
                rTitle.setBold(true);
                rTitle.setFontSize(24);
                rTitle.setColor("2C3E50");
                rTitle.setFontFamily("Arial");
                rTitle.addCarriageReturn();
                
                // Subtítulo Proyecto
                org.apache.poi.xwpf.usermodel.XWPFParagraph pSub = dummyDoc.createParagraph();
                pSub.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
                org.apache.poi.xwpf.usermodel.XWPFRun rSub = pSub.createRun();
                rSub.setText("Proyecto: ${proyecto_nombre} (${proyecto_codigo})");
                rSub.setFontSize(14);
                rSub.setColor("7F8C8D");
                rSub.setItalic(true);
                rSub.setFontFamily("Arial");
                
                // Salto de línea
                dummyDoc.createParagraph().createRun().addCarriageReturn();
                
                // Salto de página después del título
                dummyDoc.createParagraph().setPageBreak(true);
                
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                dummyDoc.write(baos);
                dummyDoc.close();
                templateStream = new java.io.ByteArrayInputStream(baos.toByteArray());
            } else {
                templateStream = templateResource.getInputStream();
            }

            try (InputStream streamToUse = templateStream) {
                byte[] documentBytes = ersExportService.generateErsDocument(projectId, streamToUse);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
                headers.setContentDispositionFormData("attachment", "ERS_Proyecto_" + projectId + ".docx");
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                return new ResponseEntity<>(documentBytes, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sw.toString().getBytes());
        }
    }
}
