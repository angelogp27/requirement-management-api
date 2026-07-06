package pe.api.requirementmanagementapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import pe.api.requirementmanagementapi.model.Proyecto;
import pe.api.requirementmanagementapi.model.Requisito;
import pe.api.requirementmanagementapi.repository.ProyectoRepository;
import pe.api.requirementmanagementapi.repository.RequisitoRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ErsExportService {

    private final ProyectoRepository proyectoRepository;
    private final RequisitoRepository requisitoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generates the ERS document replacing placeholders with dynamically assembled content.
     */
    public String generateErsMarkdownPreview(UUID proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        List<Requisito> requisitos = requisitoRepository.findByProyectoId(proyectoId);

        StringBuilder seccion31Interfaces = new StringBuilder();
        StringBuilder seccion32Funciones = new StringBuilder();
        StringBuilder seccion33Rendimiento = new StringBuilder();
        StringBuilder seccion34Restricciones = new StringBuilder();
        StringBuilder seccion35Atributos = new StringBuilder();
        StringBuilder anexoReglasNegocio = new StringBuilder();

        for (Requisito req : requisitos) {
            String tipo = req.getTipo().name();
            if ("IE".equals(tipo)) {
                procesarRequisitoSimplePreview(req, seccion31Interfaces);
            } else if ("RF".equals(tipo)) {
                procesarSeccionFuncionesPreview(req, seccion32Funciones);
            } else if ("RNF_RENDIMIENTO".equals(tipo)) {
                procesarRequisitoSimplePreview(req, seccion33Rendimiento);
            } else if ("RNF_DISENO".equals(tipo)) {
                procesarRequisitoSimplePreview(req, seccion34Restricciones);
            } else if ("RNF_CALIDAD".equals(tipo)) {
                procesarRequisitoSimplePreview(req, seccion35Atributos);
            } else if ("BR".equals(tipo)) {
                procesarRequisitoSimplePreview(req, anexoReglasNegocio);
            } else if ("RNF".equals(tipo)) {
                // Compatibilidad
                procesarRequisitoSimplePreview(req, seccion35Atributos);
            }
        }

        if (seccion31Interfaces.length() == 0) seccion31Interfaces.append("<p>No hay interfaces externas especificadas.</p>\n");
        if (seccion33Rendimiento.length() == 0) seccion33Rendimiento.append("<p>No hay requisitos de rendimiento especificados.</p>\n");
        if (seccion34Restricciones.length() == 0) seccion34Restricciones.append("<p>No hay restricciones de diseño especificadas.</p>\n");
        if (seccion35Atributos.length() == 0) seccion35Atributos.append("<p>No hay otros atributos de calidad especificados.</p>\n");
        if (anexoReglasNegocio.length() == 0) anexoReglasNegocio.append("<p>No hay reglas de negocio definidas.</p>\n");

        String rawPreview = 
               "<h2>3. Requisitos Espec&iacute;ficos</h2>\n" +
               "<h3>3.1 Interfaces Externas</h3>\n" + seccion31Interfaces.toString() + "\n" +
               "<h3>3.2 Requisitos Funcionales</h3>\n" + seccion32Funciones.toString() + "\n" +
               "<h3>3.3 Requisitos de Rendimiento</h3>\n" + seccion33Rendimiento.toString() + "\n" +
               "<h3>3.4 Restricciones de Diseño</h3>\n" + seccion34Restricciones.toString() + "\n" +
               "<h3>3.5 Atributos de Calidad</h3>\n" + seccion35Atributos.toString() + "\n" +
               "<h3>Anexo: Reglas de Negocio</h3>\n" + anexoReglasNegocio.toString() + "\n";

        return "<p><strong>--- INICIO REQUISITOS GENERADOS ---</strong></p>\n" + rawPreview + "\n<p><strong>--- FIN REQUISITOS GENERADOS ---</strong></p>";
    }

    private void procesarRequisitoSimplePreview(Requisito req, StringBuilder sb) {
        sb.append("<p><strong>[").append(req.getCodigo()).append("]:</strong> ").append(req.getDescripcion()).append("</p>\n");
    }

    private void procesarSeccionFuncionesPreview(Requisito req, StringBuilder sb) {
        sb.append("<p><strong>Requisito: ").append(req.getCodigo()).append(" - ").append(req.getDescripcion()).append("</strong></p>\n<ul>");

        Map<String, Object> detalles = req.getDetallesCasoUso();
        if (detalles == null) {
            sb.append("</ul>\n");
            return;
        }

        String useCaseId = getString(detalles, "id");
        if (!useCaseId.equals("N/A")) {
            sb.append("<li><b>ID Caso de Uso:</b> ").append(useCaseId).append("</li>\n");
        }

        if ("ALTA".equalsIgnoreCase(req.getNivelCeremonia())) {
            sb.append("<li><b>Nivel:</b> Alta Ceremonia</li>\n");
            sb.append("<li><b>Nombre:</b> ").append(getString(detalles, "nombre")).append("</li>\n");
            sb.append("<li><b>Actor Principal:</b> ").append(getString(detalles, "actorPrincipal")).append("</li>\n");
            sb.append("<li><b>Actores Secundarios:</b> ").append(getString(detalles, "actoresSecundarios")).append("</li>\n");
            sb.append("<li><b>Disparador:</b> ").append(getString(detalles, "disparador")).append("</li>\n");
            sb.append("<li><b>Precondiciones:</b> ").append(getString(detalles, "precondiciones")).append("</li>\n");
            sb.append("<li><b>Garantías Mínimas:</b> ").append(getString(detalles, "garantiasMinimas")).append("</li>\n");
            sb.append("<li><b>Garantías de Éxito:</b> ").append(getString(detalles, "garantiasExito")).append("</li>\n");
            sb.append("<li><b>Flujo Principal:</b> <br>").append(getString(detalles, "flujoBasico").replace("\n", "<br>")).append("</li>\n");
            sb.append("<li><b>Flujos Alternativos:</b> <br>").append(getString(detalles, "flujosAlternativos").replace("\n", "<br>")).append("</li>\n");
            sb.append("<li><b>Excepciones:</b> <br>").append(getString(detalles, "excepciones").replace("\n", "<br>")).append("</li>\n");
            sb.append("<li><b>Notas:</b> ").append(getString(detalles, "notas")).append("</li>\n");
            sb.append("<li><b>Historial:</b> ").append(getString(detalles, "historialCambios")).append("</li>\n");
        } else {
            sb.append("<li><b>Nivel:</b> Baja Ceremonia</li>\n");
            sb.append("<li><b>Nombre:</b> ").append(getString(detalles, "nombre")).append("</li>\n");
            sb.append("<li><b>Objetivo:</b> ").append(getString(detalles, "objetivo")).append("</li>\n");
            sb.append("<li><b>Actor Principal:</b> ").append(getString(detalles, "actorPrincipal")).append("</li>\n");
            sb.append("<li><b>Actores Secundarios:</b> ").append(getString(detalles, "actoresSecundarios")).append("</li>\n");
            sb.append("<li><b>Precondiciones:</b> ").append(getString(detalles, "precondiciones")).append("</li>\n");
            sb.append("<li><b>Flujo Básico:</b> <br>").append(getString(detalles, "flujoBasico").replace("\n", "<br>")).append("</li>\n");
            sb.append("<li><b>Postcondiciones:</b> ").append(getString(detalles, "postcondiciones")).append("</li>\n");
            sb.append("<li><b>Excepciones:</b> <br>").append(getString(detalles, "excepciones").replace("\n", "<br>")).append("</li>\n");
        }
        sb.append("</ul>\n");
    }

    public byte[] generateErsDocument(UUID proyectoId, InputStream templateInputStream) throws Exception {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        List<Requisito> requisitos = requisitoRepository.findByProyectoId(proyectoId);

        // Load the Word document
        try (XWPFDocument document = new XWPFDocument(templateInputStream)) {
            
            // Replace basic placeholders in title page
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replacePlaceholder(paragraph, "${proyecto_nombre}", proyecto.getNombre());
                replacePlaceholder(paragraph, "${proyecto_codigo}", proyecto.getCodigo());
            }

            String ersHtml = proyecto.getErsMarkdown();
            if (ersHtml != null && !ersHtml.isEmpty()) {
                String preview = generateErsMarkdownPreview(proyectoId);
                // Regex para insertar preview justo ANTES del título 4. Apéndices (ya sea <h2>, <h1>, <p><strong>)
                String regexApendices = "(?i)(<(?:h[1-6]|p)[^>]*>\\s*(?:<strong>)?\\s*4\\.\\s*Ap(?:&eacute;|é)ndices\\s*(?:</strong>)?\\s*</(?:h[1-6]|p)>)";
                
                if (ersHtml.matches("(?s).*" + regexApendices + ".*")) {
                    ersHtml = ersHtml.replaceAll(regexApendices, preview + "\n$1");
                } else {
                    ersHtml = ersHtml + preview;
                }
                parseHtmlToDocx(ersHtml, document);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();
        }
    }

    private void appendSectionHeader(XWPFDocument doc, String text, String color, int size) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(200);
        p.setSpacingAfter(200);
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setBold(true);
        r.setColor(color);
        r.setFontSize(size);
        r.setFontFamily("Arial");
    }

    private void appendSimpleParagraph(XWPFDocument doc, String text) {
        if (text == null || text.isEmpty()) return;
        String[] lines = text.split("\n");
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setFontSize(11);
        r.setFontFamily("Arial");
        for (int i = 0; i < lines.length; i++) {
            r.setText(lines[i]);
            if (i < lines.length - 1) r.addCarriageReturn();
        }
    }

    private void parseHtmlToDocx(String html, XWPFDocument document) {
        if (html == null || html.isEmpty()) return;
        org.jsoup.nodes.Document jsoupDoc = org.jsoup.Jsoup.parseBodyFragment(html);
        for (org.jsoup.nodes.Element element : jsoupDoc.body().children()) {
            if (element.tagName().equals("hr")) {
                XWPFParagraph p = document.createParagraph();
                p.setBorderBottom(Borders.SINGLE);
                continue;
            }
            String tagName = element.tagName().toLowerCase();
            if (tagName.equals("ul") || tagName.equals("ol")) {
                for (org.jsoup.nodes.Element li : element.children()) {
                    if (li.tagName().equals("li")) {
                        XWPFParagraph p = document.createParagraph();
                        p.setIndentationLeft(720);
                        XWPFRun bulletRun = p.createRun();
                        bulletRun.setText("• ");
                        bulletRun.setFontFamily("Arial");
                        processHtmlNodes(li, p);
                    }
                }
                continue;
            }
            
            XWPFParagraph paragraph = document.createParagraph();
            if (tagName.equals("h1")) paragraph.setStyle("Heading1");
            else if (tagName.equals("h2")) paragraph.setStyle("Heading2");
            else if (tagName.equals("h3")) paragraph.setStyle("Heading3");
            else if (tagName.equals("li")) paragraph.setIndentationLeft(720);
            
            processHtmlNodes(element, paragraph);
        }
    }

    private void processHtmlNodes(org.jsoup.nodes.Node parentNode, XWPFParagraph paragraph) {
        XWPFDocument document = paragraph.getDocument();
        for (org.jsoup.nodes.Node node : parentNode.childNodes()) {
            if (node instanceof org.jsoup.nodes.TextNode) {
                org.jsoup.nodes.TextNode textNode = (org.jsoup.nodes.TextNode) node;
                if (!textNode.isBlank()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(textNode.text());
                    if (node.parent() instanceof org.jsoup.nodes.Element) {
                        applyInlineStyles((org.jsoup.nodes.Element) node.parent(), run);
                    }
                }
            } else if (node instanceof org.jsoup.nodes.Element) {
                org.jsoup.nodes.Element element = (org.jsoup.nodes.Element) node;
                if (element.tagName().equals("br")) {
                    paragraph.createRun().addBreak();
                } else if (element.tagName().equals("p") || element.tagName().equals("div")) {
                    XWPFParagraph p = document.createParagraph();
                    processHtmlNodes(element, p);
                } else if (element.tagName().equals("ul") || element.tagName().equals("ol")) {
                    for (org.jsoup.nodes.Element li : element.children()) {
                        if (li.tagName().equals("li")) {
                            XWPFParagraph p = document.createParagraph();
                            p.setIndentationLeft(720);
                            
                            XWPFRun bulletRun = p.createRun();
                            bulletRun.setText("• ");
                            bulletRun.setFontFamily("Arial");
                            
                            processHtmlNodes(li, p);
                        }
                    }
                } else {
                    processHtmlNodes(element, paragraph);
                }
            }
        }
    }

    private void applyInlineStyles(org.jsoup.nodes.Element parentElement, XWPFRun run) {
        run.setFontFamily("Arial");
        run.setFontSize(11); // default
        String tagName = parentElement.tagName().toLowerCase();
        if (tagName.equals("strong") || tagName.equals("b") || tagName.matches("h[1-6]")) run.setBold(true);
        if (tagName.equals("em") || tagName.equals("i")) run.setItalic(true);
        if (tagName.equals("u")) run.setUnderline(UnderlinePatterns.SINGLE);
        
        if (tagName.equals("h1")) run.setFontSize(24);
        else if (tagName.equals("h2")) run.setFontSize(18);
        else if (tagName.equals("h3")) run.setFontSize(14);
        
        org.jsoup.nodes.Element parent = parentElement.parent();
        while (parent != null && !parent.tagName().equals("body") && !parent.isBlock()) {
            String pTag = parent.tagName().toLowerCase();
            if (pTag.equals("strong") || pTag.equals("b")) run.setBold(true);
            if (pTag.equals("em") || pTag.equals("i")) run.setItalic(true);
            if (pTag.equals("u")) run.setUnderline(UnderlinePatterns.SINGLE);
            parent = parent.parent();
        }
    }

    private void replacePlaceholder(XWPFParagraph paragraph, String placeholder, String value) {
        String text = paragraph.getText();
        if (text != null && text.contains(placeholder)) {
            for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }
            XWPFRun newRun = paragraph.createRun();
            String replacedText = text.replace(placeholder, value);
            String[] lines = replacedText.split("\n");
            for (int i = 0; i < lines.length; i++) {
                newRun.setText(lines[i]);
                if (i < lines.length - 1) newRun.addCarriageReturn();
            }
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "N/A";
    }
}
