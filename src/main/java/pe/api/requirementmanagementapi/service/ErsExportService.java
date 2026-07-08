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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.util.Units;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        return "<div style=\"display:none;\" class=\"req-marker\">--- INICIO REQUISITOS GENERADOS ---</div>\n" + rawPreview + "\n<div style=\"display:none;\" class=\"req-marker\">--- FIN REQUISITOS GENERADOS ---</div>";
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
            
            // Clear template contents to avoid double cover page
            for (int i = document.getBodyElements().size() - 1; i >= 0; i--) {
                document.removeBodyElement(i);
            }

            String ersHtml = proyecto.getErsMarkdown();
            if (ersHtml != null && !ersHtml.isEmpty()) {
                // Generar Portada, Ficha de Documento y Encabezado
                createCoverPage(document, proyecto);
                createDocumentInfoSheet(document, proyecto);
                // Set Different First Page to hide page number on the cover
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr sectPr = document.getDocument().getBody().getSectPr();
                if (sectPr == null) sectPr = document.getDocument().getBody().addNewSectPr();
                sectPr.addNewTitlePg().setVal(Boolean.TRUE);

                // Add Page Break BEFORE TOC to put it in a new section/page
                XWPFParagraph pBreakBeforeToc = document.createParagraph();
                pBreakBeforeToc.createRun().addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);

                // Add TOC
                XWPFParagraph pToc = document.createParagraph();
                pToc.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun rToc = pToc.createRun();
                rToc.setText("Índice");
                rToc.setBold(true);
                rToc.setFontSize(18);
                rToc.setColor("2F5496");
                rToc.setFontFamily("Calibri");
                
                XWPFParagraph tocNote = document.createParagraph();
                XWPFRun rTocNote = tocNote.createRun();
                rTocNote.setText("(Haga clic derecho en esta área y seleccione 'Actualizar campos' para generar el índice automático)");
                rTocNote.setItalic(true);
                rTocNote.setColor("808080");
                rTocNote.setFontSize(10);
                
                XWPFParagraph tocCode = document.createParagraph();
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField fld = tocCode.getCTP().addNewFldSimple();
                fld.setInstr("TOC \\o \"1-3\" \\h \\z \\u");
                
                XWPFParagraph pBreak = document.createParagraph();
                pBreak.createRun().addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);

                createHeader(document, proyecto);
                createFooter(document);
                // Remove document.enforceUpdateFields() to prevent the popup on open

                String preview = generateErsMarkdownPreview(proyectoId);
                // Regex para insertar preview justo ANTES del título 4. Apéndices (ya sea <h2>, <h1>, <p><strong>)
                String regexApendices = "(?i)(<(?:h[1-6]|p)[^>]*>\\s*(?:<strong>)?\\s*4\\.\\s*Ap(?:&eacute;|é)ndices\\s*(?:</strong>)?\\s*</(?:h[1-6]|p)>)";
                
                if (ersHtml.matches("(?s).*" + regexApendices + ".*")) {
                    ersHtml = ersHtml.replaceAll(regexApendices, preview + "\n$1");
                } else {
                    ersHtml = ersHtml + preview;
                }
                // Strip redundant default template title and project so they don't duplicate the cover page
                ersHtml = ersHtml.replaceAll("(?is)<h[1-6][^>]*>\\s*Especificaci(?:&oacute;|ó)n de Requisitos de Software \\(ERS\\)\\s*</h[1-6]>", "");
                ersHtml = ersHtml.replaceAll("(?is)<h[1-6][^>]*>\\s*(?:<strong>)?\\s*Proyecto:\\s*.*?(?:</strong>)?\\s*</h[1-6]>", "");
                ersHtml = ersHtml.replaceAll("(?is)<p[^>]*>\\s*(?:<strong>)?\\s*Proyecto:\\s*.*?(?:</strong>)?\\s*</p>", "");
                // Also explicitly remove the exact text if it leaked as raw text
                String exactProjectText = "Proyecto: " + proyecto.getNombre() + " (" + proyecto.getCodigo() + ")";
                ersHtml = ersHtml.replace(exactProjectText, "");
                
                // Remove the hidden markers from the Word Export
                ersHtml = ersHtml.replaceAll("(?is)<div[^>]*>\\s*--- INICIO REQUISITOS GENERADOS ---\\s*</div>", "");
                ersHtml = ersHtml.replaceAll("(?is)<div[^>]*>\\s*--- FIN REQUISITOS GENERADOS ---\\s*</div>", "");

                // Remove any leading <hr> or <br> that might be left orphaned at the start of the document
                ersHtml = ersHtml.replaceFirst("(?is)^\\s*(?:<br\\s*/?>\\s*|<hr\\s*/?>\\s*)+", "");

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
            paragraph.setSpacingAfter(200); // 10 pt spacing after paragraph
            
            if (tagName.equals("h1")) {
                paragraph.setStyle("Heading1");
                paragraph.setPageBreak(true);
            } else if (tagName.equals("h2")) {
                paragraph.setStyle("Heading2");
                paragraph.setSpacingBefore(400);
                if (element.text().matches("^[1-9]\\.\\s.*") || element.text().toLowerCase().contains("apéndice")) {
                    paragraph.setPageBreak(true);
                }
            } else if (tagName.equals("h3")) {
                paragraph.setStyle("Heading3");
                paragraph.setSpacingBefore(200);
            } else if (tagName.equals("li")) {
                paragraph.setIndentationLeft(720);
                paragraph.setSpacingAfter(100);
            }
            
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
                    p.setSpacingAfter(200);
                    processHtmlNodes(element, p);
                } else if (element.tagName().equals("ul") || element.tagName().equals("ol")) {
                    for (org.jsoup.nodes.Element li : element.children()) {
                        if (li.tagName().equals("li")) {
                            XWPFParagraph p = document.createParagraph();
                            p.setIndentationLeft(720);
                            p.setSpacingAfter(100);
                            
                            XWPFRun bulletRun = p.createRun();
                            bulletRun.setText("• ");
                            bulletRun.setFontFamily("Calibri");
                            
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
        run.setFontFamily("Calibri");
        run.setFontSize(11); // default
        String tagName = parentElement.tagName().toLowerCase();
        if (tagName.equals("strong") || tagName.equals("b") || tagName.matches("h[1-6]")) run.setBold(true);
        if (tagName.equals("em") || tagName.equals("i")) run.setItalic(true);
        if (tagName.equals("u")) run.setUnderline(UnderlinePatterns.SINGLE);
        
        if (tagName.matches("h[1-6]")) run.setColor("2F5496");
        if (tagName.equals("h1")) run.setFontSize(22);
        else if (tagName.equals("h2")) run.setFontSize(18);
        else if (tagName.equals("h3")) run.setFontSize(14);
        
        org.jsoup.nodes.Element parent = parentElement.parent();
        while (parent != null && !parent.tagName().equals("body") && !parent.isBlock()) {
            String pTag = parent.tagName().toLowerCase();
            if (pTag.equals("strong") || pTag.equals("b")) run.setBold(true);
            if (pTag.equals("em") || pTag.equals("i")) run.setItalic(true);
            if (pTag.equals("u")) run.setUnderline(UnderlinePatterns.SINGLE);
            if (pTag.matches("h[1-6]")) {
                run.setColor("2F5496");
                if (pTag.equals("h1")) run.setFontSize(22);
                else if (pTag.equals("h2")) run.setFontSize(18);
                else if (pTag.equals("h3")) run.setFontSize(14);
            }
            parent = parent.parent();
        }
    }

    private void createCoverPage(XWPFDocument document, Proyecto proyecto) {
        XWPFParagraph pTitle = document.createParagraph();
        pTitle.setAlignment(ParagraphAlignment.CENTER);
        pTitle.setSpacingBefore(3000);
        XWPFRun rTitle = pTitle.createRun();
        rTitle.setText("Documento de Especificación de Requisitos (ERS)");
        rTitle.setBold(true);
        rTitle.setFontSize(36);
        rTitle.setColor("000000"); // Letra formal negra grande
        rTitle.setFontFamily("Calibri");

        XWPFParagraph pProject = document.createParagraph();
        pProject.setAlignment(ParagraphAlignment.CENTER);
        pProject.setSpacingBefore(1000);
        XWPFRun rProject = pProject.createRun();
        rProject.setText("Proyecto: " + proyecto.getNombre());
        rProject.setBold(true);
        rProject.setFontSize(22);
        rProject.setColor("000000");
        rProject.setFontFamily("Calibri");

        XWPFParagraph pInfo = document.createParagraph();
        pInfo.setAlignment(ParagraphAlignment.CENTER);
        pInfo.setSpacingBefore(3000);
        XWPFRun rInfo = pInfo.createRun();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDate = LocalDate.now().format(formatter);
        rInfo.setText("Fecha: " + currentDate);
        rInfo.addBreak();
        
        String managerName = "No Asignado";
        if (proyecto.getJefeProyecto() != null && proyecto.getJefeProyecto().getUsername() != null) {
            managerName = proyecto.getJefeProyecto().getUsername();
        }
        rInfo.setText("Jefe de Proyecto: " + managerName);
        rInfo.setFontSize(14);
        rInfo.setFontFamily("Calibri");
        
        rInfo.addBreak(BreakType.PAGE);
    }

    private void createDocumentInfoSheet(XWPFDocument document, Proyecto proyecto) {
        XWPFParagraph pTitle = document.createParagraph();
        pTitle.setStyle("Heading1");
        XWPFRun rTitle = pTitle.createRun();
        rTitle.setText("Ficha del documento");
        rTitle.setBold(true);
        rTitle.setFontSize(18);
        rTitle.setColor("2F5496");
        rTitle.setFontFamily("Calibri");

        pTitle.setSpacingAfter(400);

        XWPFTable table = document.createTable(3, 4);
        table.setWidth("100%");
        
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {"Fecha", "Revisión", "Autor", "Modificación"};
        for (int i = 0; i < 4; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.setColor("D9D9D9");
            XWPFParagraph p = cell.getParagraphArray(0);
            p.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r = p.createRun();
            r.setText(headers[i]);
            r.setBold(true);
            r.setFontFamily("Calibri");
        }

        for (int i = 1; i <= 2; i++) {
            XWPFTableRow row = table.getRow(i);
            row.getCell(0).setText("[Fecha]");
            row.getCell(1).setText("[Rev]");
            row.getCell(2).setText("[Descripcion]");
            row.getCell(3).setText("[Descripcion]");
            
            for (int j = 0; j < 4; j++) {
                XWPFParagraph p = row.getCell(j).getParagraphArray(0);
                if (p.getRuns().size() > 0) {
                    XWPFRun r = p.getRuns().get(0);
                    r.setColor("0000FF");
                    r.setItalic(true);
                    r.setFontFamily("Calibri");
                }
            }
        }

        XWPFParagraph pText = document.createParagraph();
        pText.setSpacingBefore(1000);
        pText.setSpacingAfter(1000);
        XWPFRun rText = pText.createRun();
        rText.setFontFamily("Calibri");
        rText.setText("Documento validado por las partes en fecha: ");
        
        XWPFRun rDatePlaceholder = pText.createRun();
        rDatePlaceholder.setText("[Fecha]");
        rDatePlaceholder.setColor("0000FF");
        rDatePlaceholder.setItalic(true);
        rDatePlaceholder.setFontFamily("Calibri");

        XWPFTable sigTable = document.createTable(1, 2);
        sigTable.setWidth("100%");
        sigTable.getCTTbl().getTblPr().unsetTblBorders();
        
        XWPFTableRow sigRow = sigTable.getRow(0);
        
        XWPFTableCell clientCell = sigRow.getCell(0);
        XWPFParagraph pc1 = clientCell.getParagraphArray(0);
        pc1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun rc1 = pc1.createRun();
        rc1.setText("Por el cliente");
        rc1.setFontFamily("Calibri");
        
        XWPFParagraph pc2 = clientCell.addParagraph();
        pc2.setSpacingBefore(500);
        XWPFRun rc2 = pc2.createRun();
        rc2.setText("[Firma]");
        rc2.setFontFamily("Calibri");
        
        XWPFParagraph pc3 = clientCell.addParagraph();
        pc3.setSpacingBefore(1000);
        XWPFRun rc3 = pc3.createRun();
        rc3.setText("_______________________________________");
        
        XWPFParagraph pc4 = clientCell.addParagraph();
        XWPFRun rc4 = pc4.createRun();
        rc4.setText("Sr./Sra. ");
        rc4.setFontFamily("Calibri");
        XWPFRun rc4_2 = pc4.createRun();
        rc4_2.setText("[Nombre]");
        rc4_2.setColor("0000FF");
        rc4_2.setFontFamily("Calibri");
        
        XWPFTableCell provCell = sigRow.getCell(1);
        XWPFParagraph pp1 = provCell.getParagraphArray(0);
        pp1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun rp1 = pp1.createRun();
        rp1.setText("Por la empresa suministradora");
        rp1.setFontFamily("Calibri");
        
        XWPFParagraph pp2 = provCell.addParagraph();
        pp2.setSpacingBefore(500);
        XWPFRun rp2 = pp2.createRun();
        rp2.setText("[Firma]");
        rp2.setFontFamily("Calibri");
        
        XWPFParagraph pp3 = provCell.addParagraph();
        pp3.setSpacingBefore(1000);
        XWPFRun rp3 = pp3.createRun();
        rp3.setText("_______________________________________");
        
        XWPFParagraph pp4 = provCell.addParagraph();
        XWPFRun rp4 = pp4.createRun();
        rp4.setText("Sr./Sra. ");
        rp4.setFontFamily("Calibri");
        XWPFRun rp4_2 = pp4.createRun();
        rp4_2.setText("[Nombre]");
        rp4_2.setColor("0000FF");
        rp4_2.setFontFamily("Calibri");
        rp4_2.addBreak(BreakType.PAGE);
    }

    private void createHeader(XWPFDocument document, Proyecto proyecto) {
        try {
            org.apache.poi.wp.usermodel.HeaderFooterType[] types = {
                org.apache.poi.wp.usermodel.HeaderFooterType.FIRST, 
                org.apache.poi.wp.usermodel.HeaderFooterType.DEFAULT
            };
            
            for (org.apache.poi.wp.usermodel.HeaderFooterType type : types) {
                XWPFHeader header = document.createHeader(type);
                XWPFTable table = header.createTable(1, 2);
                table.setWidth("100%");
                table.getCTTbl().getTblPr().unsetTblBorders();
                table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
                
                XWPFTableCell leftCell = table.getRow(0).getCell(0);
                XWPFParagraph pLeft = leftCell.getParagraphArray(0);
                pLeft.setAlignment(ParagraphAlignment.LEFT);
                pLeft.setSpacingAfter(0);
                XWPFRun rLeft = pLeft.createRun();
                
                org.springframework.core.io.ClassPathResource imgResource = new org.springframework.core.io.ClassPathResource("img/logo.png");
                if (imgResource.exists()) {
                    try (java.io.InputStream is = imgResource.getInputStream()) {
                        rLeft.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, "logo.png", org.apache.poi.util.Units.toEMU(120), org.apache.poi.util.Units.toEMU(40));
                    }
                } else {
                    rLeft.setText("[Logo]");
                }
                
                XWPFTableCell rightCell = table.getRow(0).getCell(1);
                XWPFParagraph pRight = rightCell.getParagraphArray(0);
                pRight.setAlignment(ParagraphAlignment.RIGHT);
                pRight.setSpacingAfter(0);
                XWPFRun rRight1 = pRight.createRun();
                rRight1.setText("Especificación de Requisitos, estándar de IEEE 830");
                rRight1.setFontFamily("Calibri");
                rRight1.setFontSize(10);
                rRight1.addBreak();
                XWPFRun rRight2 = pRight.createRun();
                rRight2.setText(proyecto.getNombre());
                rRight2.setFontFamily("Calibri");
                rRight2.setFontSize(10);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFooter(XWPFDocument document) {
        try {
            org.apache.poi.xwpf.usermodel.XWPFFooter footer = document.createFooter(org.apache.poi.wp.usermodel.HeaderFooterType.DEFAULT);
            XWPFParagraph p = footer.createParagraph();
            p.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun r = p.createRun();
            r.setText("Página ");
            r.setFontFamily("Calibri");
            p.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
        } catch (Exception e) {
            e.printStackTrace();
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
