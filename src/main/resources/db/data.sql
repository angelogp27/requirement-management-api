-- ============================================================
-- SCRIPT DE DATOS DE PRUEBA (SEED) MASIVO - Sistema REM
-- Archivo: src/main/resources/db/data.sql
-- ============================================================

-- ============================================================
-- 0. LIMPIEZA DE DATOS EXISTENTES
-- ============================================================
TRUNCATE TABLE requisitos_versiones, 
               change_requests, 
               checklist_validacion, 
               comentarios_review, 
               requisitos_dependencias, 
               requisitos_historial, 
               requisitos_stakeholders, 
               requisitos, 
               stakeholders, 
               proyectos, 
               usuarios CASCADE;

-- ============================================================
-- 1. USUARIOS (10 Usuarios)
-- ============================================================
INSERT INTO usuarios (id, username, password, email, rol, activo) VALUES
('11111111-1111-1111-1111-111111111111', 'jefe_admin', 'secret', 'jefe.admin@empresa.com', 'JEFE_PROYECTO', true),
('11111111-1111-1111-1111-111111111112', 'jefe_oper', 'secret', 'jefe.oper@empresa.com', 'JEFE_PROYECTO', true),
('22222222-2222-2222-2222-222222222221', 'analista_carlos', 'secret', 'acarlos@empresa.com', 'ANALISTA', true),
('22222222-2222-2222-2222-222222222222', 'analista_maria', 'secret', 'amaria@empresa.com', 'ANALISTA', true),
('22222222-2222-2222-2222-222222222223', 'analista_jorge', 'secret', 'ajorge@empresa.com', 'ANALISTA', true),
('33333333-3333-3333-3333-333333333331', 'dev_pedro', 'secret', 'dpedro@empresa.com', 'TECNICO', true),
('33333333-3333-3333-3333-333333333332', 'dev_lucia', 'secret', 'dlucia@empresa.com', 'TECNICO', true),
('33333333-3333-3333-3333-333333333333', 'qa_andres', 'secret', 'qandres@empresa.com', 'TECNICO', true),
('33333333-3333-3333-3333-333333333334', 'dev_sofia', 'secret', 'dsofia@empresa.com', 'TECNICO', true),
('33333333-3333-3333-3333-333333333335', 'qa_martin', 'secret', 'qmartin@empresa.com', 'TECNICO', true);

-- ============================================================
-- 2. PROYECTOS (4 Proyectos)
-- ============================================================
INSERT INTO proyectos (id, codigo, nombre, descripcion, jefe_proyecto_id, analista_id, estado, ers_markdown) VALUES
('44444444-4444-4444-4444-444444444441', 'PRJ-ECO-01', 'Plataforma E-commerce Global', 'Desarrollo de un marketplace para venta de productos masivos con integracion de multiples pasarelas de pago y logistica internacional.', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222221', 'ACTIVO', '# ERS E-commerce\n\nEste documento describe los requisitos para la plataforma E-commerce global. Incluye modulos de catalogo, carrito de compras, pagos y logistica.'),
('44444444-4444-4444-4444-444444444442', 'PRJ-HRM-02', 'Sistema de Recursos Humanos (HRM)', 'Modernizacion del sistema de planillas, evaluacion de desempeno y gestion de vacaciones para los 5,000 empleados de la organizacion.', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'ACTIVO', '# ERS Recursos Humanos\n\nEl sistema HRM consolidara la informacion del personal, nominas y vacaciones.'),
('44444444-4444-4444-4444-444444444443', 'PRJ-CRM-03', 'CRM para Ventas Corporativas', 'Implementacion de un CRM para gestionar el embudo de ventas B2B, seguimiento de leads y metricas de conversion.', '11111111-1111-1111-1111-111111111112', '22222222-2222-2222-2222-222222222223', 'PAUSADO', '# ERS CRM\n\nProyecto actualmente pausado debido a recorte de presupuesto.'),
('44444444-4444-4444-4444-444444444444', 'PRJ-AI-04', 'Asistente de IA para Soporte', 'Chatbot con LLM integrado para reducir el volumen de tickets de soporte tecnico de nivel 1 en un 40%.', '11111111-1111-1111-1111-111111111112', '22222222-2222-2222-2222-222222222221', 'ACTIVO', '# ERS Asistente IA\n\nEl asistente estara disponible 24/7 y aprendera de la base de conocimientos interna.');

-- ============================================================
-- 3. STAKEHOLDERS (12 Stakeholders)
-- ============================================================
-- Proyecto 1 (E-commerce)
INSERT INTO stakeholders (id, proyecto_id, nombre, rol, organizacion, nivel_influencia, contacto) VALUES
('66666666-6666-6666-6666-666666666661', '44444444-4444-4444-4444-444444444441', 'Elena Vasquez', 'CEO', 'Empresa Retail', 'ALTO', 'elena.v@retail.com'),
('66666666-6666-6666-6666-666666666662', '44444444-4444-4444-4444-444444444441', 'Marcos Torres', 'Gerente Logistica', 'Empresa Retail', 'MEDIO', 'marcos.t@retail.com'),
('66666666-6666-6666-6666-666666666663', '44444444-4444-4444-4444-444444444441', 'Laura Jimenez', 'Auditor de Seguridad', 'Consultora Externa', 'MEDIO', 'laura.j@sec.com'),
('66666666-6666-6666-6666-666666666664', '44444444-4444-4444-4444-444444444441', 'Cliente Final', 'Comprador Anonimo', 'N/A', 'BAJO', 'N/A');

-- Proyecto 2 (HRM)
INSERT INTO stakeholders (id, proyecto_id, nombre, rol, organizacion, nivel_influencia, contacto) VALUES
('66666666-6666-6666-6666-666666666665', '44444444-4444-4444-4444-444444444442', 'Ricardo Gomez', 'Director de RRHH', 'Empresa Tech', 'ALTO', 'ricardo.g@tech.com'),
('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444442', 'Patricia Castro', 'Analista de Nominas', 'Empresa Tech', 'MEDIO', 'patricia.c@tech.com'),
('66666666-6666-6666-6666-666666666667', '44444444-4444-4444-4444-444444444442', 'Sindicato de Trabajadores', 'Representante', 'Sindicato', 'ALTO', 'contacto@sindicato.org');

-- Proyecto 3 (CRM)
INSERT INTO stakeholders (id, proyecto_id, nombre, rol, organizacion, nivel_influencia, contacto) VALUES
('66666666-6666-6666-6666-666666666668', '44444444-4444-4444-4444-444444444443', 'Diego Salas', 'VP de Ventas', 'Empresa Tech', 'ALTO', 'diego.s@tech.com'),
('66666666-6666-6666-6666-666666666669', '44444444-4444-4444-4444-444444444443', 'Valeria Rojas', 'Key Account Manager', 'Empresa Tech', 'MEDIO', 'valeria.r@tech.com');

-- Proyecto 4 (IA)
INSERT INTO stakeholders (id, proyecto_id, nombre, rol, organizacion, nivel_influencia, contacto) VALUES
('66666666-6666-6666-6666-666666666670', '44444444-4444-4444-4444-444444444444', 'Fernando Rios', 'Director de Operaciones', 'SoporteCorp', 'ALTO', 'fernando.r@soporte.com'),
('66666666-6666-6666-6666-666666666671', '44444444-4444-4444-4444-444444444444', 'Camila Ortiz', 'Especialista en IA', 'AI Labs', 'MEDIO', 'camila.o@ailabs.com'),
('66666666-6666-6666-6666-666666666672', '44444444-4444-4444-4444-444444444444', 'Agente Soporte N1', 'Usuario Final', 'SoporteCorp', 'BAJO', 'agentes@soporte.com');

-- ============================================================
-- 4. REQUISITOS (20 Requisitos en total para varios proyectos)
-- ============================================================

-- PROYECTO 1: E-commerce (10 requisitos)
INSERT INTO requisitos (id, proyecto_id, codigo, tipo, descripcion, necesidad_cubierta, iteracion_sprint, criterios_aceptacion, solicitante_id, estado, prioridad, costo_estimado, asignado_a_id, nivel_ceremonia, detalles_caso_uso) VALUES
('88888888-8888-8888-8888-888888888801', '44444444-4444-4444-4444-444444444441', 'RF-ECO-001', 'RF', 'El sistema debe permitir a los clientes buscar productos por categorias, filtros de precio y marcas.', 'Necesidad de descubrir productos facilmente.', 'Sprint 1', '- Busqueda responde en < 1s.\n- Filtros combinables.', '22222222-2222-2222-2222-222222222221', 'APROBADO', 'CRITICA', 1200.00, '33333333-3333-3333-3333-333333333331', 'ALTA', '{"nombre": "Busqueda de Productos", "actorPrincipal": "Cliente", "flujoBasico": "1. Ingresa termino 2. Sistema muestra resultados", "precondiciones": "Tener conexion a internet", "postcondiciones": "Se muestra el listado de productos paginado"}'),
('88888888-8888-8888-8888-888888888802', '44444444-4444-4444-4444-444444444441', 'RF-ECO-002', 'RF', 'El sistema debe permitir agregar productos a un carrito de compras temporal.', 'Preparar la compra.', 'Sprint 1', '- Carrito persiste por 24 horas.\n- Limite de 50 items.', '22222222-2222-2222-2222-222222222221', 'VALIDADO', 'ALTA', 800.00, '33333333-3333-3333-3333-333333333332', 'BAJA', '{"nombre": "Gestionar Carrito", "actorPrincipal": "Cliente", "flujoBasico": "1. Click en Add to Cart 2. Carrito actualiza contador"}'),
('88888888-8888-8888-8888-888888888803', '44444444-4444-4444-4444-444444444441', 'RF-ECO-003', 'RF', 'El sistema debe procesar pagos con tarjetas Visa, Mastercard y PayPal.', 'Monetizacion del servicio.', 'Sprint 2', '- Transaccion cifrada PCI-DSS.\n- Rollback en fallo.', '22222222-2222-2222-2222-222222222221', 'EN_ANALISIS', 'CRITICA', 3500.00, NULL, 'ALTA', '{"nombre": "Procesar Pago", "actorPrincipal": "Cliente", "flujoBasico": "1. Ingresa tarjeta 2. Valida token 3. Cargo 4. Recibo"}'),
('88888888-8888-8888-8888-888888888804', '44444444-4444-4444-4444-444444444441', 'RF-ECO-004', 'RF', 'El sistema debe calcular el costo de envio basado en la distancia y peso del paquete.', 'Logistica y costos.', 'Sprint 2', '- Integracion con API de Google Maps.\n- Integracion con DHL.', '22222222-2222-2222-2222-222222222221', 'REGISTRADO', 'MEDIA', 2000.00, NULL, NULL, NULL),
('88888888-8888-8888-8888-888888888805', '44444444-4444-4444-4444-444444444441', 'RF-ECO-005', 'RF', 'El sistema debe generar una factura electronica tras el pago exitoso.', 'Cumplimiento fiscal.', 'Sprint 3', '- Envio al email en PDF.', '22222222-2222-2222-2222-222222222221', 'REGISTRADO', 'MEDIA', 900.00, NULL, NULL, NULL),
('88888888-8888-8888-8888-888888888806', '44444444-4444-4444-4444-444444444441', 'RNF-ECO-001', 'RNF', 'El tiempo de carga de la pagina principal debe ser menor a 2 segundos bajo carga normal.', 'Experiencia de usuario.', 'Sprint 1', '- Pruebas con Lighthouse score > 90.', '22222222-2222-2222-2222-222222222221', 'APROBADO', 'ALTA', 500.00, '33333333-3333-3333-3333-333333333331', NULL, NULL),
('88888888-8888-8888-8888-888888888807', '44444444-4444-4444-4444-444444444441', 'RNF-ECO-002', 'RNF', 'El sistema debe soportar 5000 usuarios concurrentes sin degradacion severa.', 'Escalabilidad.', 'Sprint 4', '- Pruebas en JMeter sin errores 500.', '22222222-2222-2222-2222-222222222221', 'REGISTRADO', 'CRITICA', 1000.00, NULL, NULL, NULL),
('88888888-8888-8888-8888-888888888808', '44444444-4444-4444-4444-444444444441', 'RNF-ECO-003', 'RNF', 'Toda comunicacion debe realizarse a traves de TLS 1.3 o superior.', 'Seguridad.', 'Sprint 1', '- Escaneo de puertos y certificados SSL A+.', '22222222-2222-2222-2222-222222222221', 'APROBADO', 'CRITICA', 300.00, '33333333-3333-3333-3333-333333333332', NULL, NULL),
('88888888-8888-8888-8888-888888888809', '44444444-4444-4444-4444-444444444441', 'RF-ECO-006', 'RF', 'El sistema debe permitir a los clientes dejar reseñas y calificaciones (1 a 5 estrellas).', 'Feedback de comunidad.', 'Sprint 3', '- Solo compradores verificados pueden opinar.', '22222222-2222-2222-2222-222222222221', 'EN_ANALISIS', 'BAJA', 600.00, NULL, 'BAJA', '{"nombre": "Dejar Reseña", "flujoBasico": "1. Cliente entra al producto 2. Escribe texto 3. Selecciona estrellas 4. Guarda"}'),
('88888888-8888-8888-8888-888888888810', '44444444-4444-4444-4444-444444444441', 'RF-ECO-007', 'RF', 'El administrador debe poder bloquear usuarios fraudulentos.', 'Control de calidad.', 'Sprint 4', '- Opcion visible en el panel admin.', '22222222-2222-2222-2222-222222222221', 'REGISTRADO', 'MEDIA', 400.00, NULL, NULL, NULL);

-- PROYECTO 2: HRM (5 requisitos)
INSERT INTO requisitos (id, proyecto_id, codigo, tipo, descripcion, necesidad_cubierta, iteracion_sprint, criterios_aceptacion, solicitante_id, estado, prioridad, costo_estimado, asignado_a_id, nivel_ceremonia, detalles_caso_uso) VALUES
('88888888-8888-8888-8888-888888888811', '44444444-4444-4444-4444-444444444442', 'RF-HRM-001', 'RF', 'El empleado debe poder solicitar vacaciones especificando rango de fechas.', 'Autogestion de empleados.', 'Sprint 1', '- Validar saldo de dias disponibles.', '22222222-2222-2222-2222-222222222222', 'APROBADO', 'ALTA', 1100.00, '33333333-3333-3333-3333-333333333334', 'ALTA', '{"nombre": "Solicitar Vacaciones", "flujoBasico": "1. Selecciona Fechas, 2. Envia, 3. Notifica jefe"}'),
('88888888-8888-8888-8888-888888888812', '44444444-4444-4444-4444-444444444442', 'RF-HRM-002', 'RF', 'El jefe directo debe poder aprobar o rechazar las vacaciones solicitadas.', 'Flujo de aprobacion.', 'Sprint 1', '- Botones de aprobar/rechazar visibles en panel.', '22222222-2222-2222-2222-222222222222', 'VALIDADO', 'ALTA', 750.00, '33333333-3333-3333-3333-333333333334', 'BAJA', '{"nombre": "Aprobar Vacaciones"}'),
('88888888-8888-8888-8888-888888888813', '44444444-4444-4444-4444-444444444442', 'RF-HRM-003', 'RF', 'El sistema debe generar el reporte de nomina mensual en Excel.', 'Cierre contable de RRHH.', 'Sprint 2', '- Exportacion en formato .xlsx.', '22222222-2222-2222-2222-222222222222', 'EN_ANALISIS', 'CRITICA', 2500.00, NULL, NULL, NULL),
('88888888-8888-8888-8888-888888888814', '44444444-4444-4444-4444-444444444442', 'RNF-HRM-001', 'RNF', 'El sistema debe cumplir con la ley de proteccion de datos personales (GDPR/LPDP).', 'Legales.', 'Sprint 1', '- Cifrado de datos sensibles en Base de Datos.', '22222222-2222-2222-2222-222222222222', 'APROBADO', 'CRITICA', 1200.00, '33333333-3333-3333-3333-333333333335', NULL, NULL),
('88888888-8888-8888-8888-888888888815', '44444444-4444-4444-4444-444444444442', 'RF-HRM-004', 'RF', 'El sistema debe notificar por correo electronico cuando una nomina es generada.', 'Comunicacion interna.', 'Sprint 2', '- Correos enviados via SendGrid.', '22222222-2222-2222-2222-222222222222', 'REGISTRADO', 'MEDIA', 400.00, NULL, NULL, NULL);

-- PROYECTO 4: IA (5 requisitos)
INSERT INTO requisitos (id, proyecto_id, codigo, tipo, descripcion, necesidad_cubierta, iteracion_sprint, criterios_aceptacion, solicitante_id, estado, prioridad, costo_estimado, asignado_a_id, nivel_ceremonia, detalles_caso_uso) VALUES
('88888888-8888-8888-8888-888888888816', '44444444-4444-4444-4444-444444444444', 'RF-AI-001', 'RF', 'El chatbot debe interpretar preguntas en lenguaje natural sobre problemas tecnicos.', 'Soporte N1 automatico.', 'Sprint 1', '- Integracion con API de OpenAI.', '22222222-2222-2222-2222-222222222221', 'APROBADO', 'CRITICA', 4000.00, '33333333-3333-3333-3333-333333333331', 'ALTA', '{"nombre": "Consultar Chatbot", "flujoBasico": "1. Usuario escribe duda, 2. Chatbot responde en menos de 3s"}'),
('88888888-8888-8888-8888-888888888817', '44444444-4444-4444-4444-444444444444', 'RF-AI-002', 'RF', 'Si el chatbot no sabe la respuesta, debe escalar el ticket a un agente humano.', 'Derivacion de problemas complejos.', 'Sprint 2', '- Crear ticket en Jira via API.', '22222222-2222-2222-2222-222222222221', 'VALIDADO', 'ALTA', 1500.00, '33333333-3333-3333-3333-333333333332', 'BAJA', '{"nombre": "Escalar a Humano"}'),
('88888888-8888-8888-8888-888888888818', '44444444-4444-4444-4444-444444444444', 'RNF-AI-001', 'RNF', 'El modelo debe tener un nivel de alucinacion (respuestas incorrectas) menor al 5%.', 'Calidad de IA.', 'Sprint 1', '- Evaluacion manual de 100 respuestas.', '22222222-2222-2222-2222-222222222221', 'EN_ANALISIS', 'CRITICA', 2000.00, NULL, NULL, NULL),
('88888888-8888-8888-8888-888888888819', '44444444-4444-4444-4444-444444444444', 'RNF-AI-002', 'RNF', 'El bot debe responder en un maximo de 3 segundos.', 'Experiencia de usuario fluida.', 'Sprint 1', '- Latencia medida desde request a response.', '22222222-2222-2222-2222-222222222221', 'APROBADO', 'ALTA', 500.00, '33333333-3333-3333-3333-333333333331', NULL, NULL),
('88888888-8888-8888-8888-888888888820', '44444444-4444-4444-4444-444444444444', 'RF-AI-003', 'RF', 'El administrador debe poder inyectar nuevos documentos de conocimiento al chatbot.', 'Entrenamiento RAG.', 'Sprint 3', '- Interfaz para subir PDFs.', '22222222-2222-2222-2222-222222222221', 'REGISTRADO', 'MEDIA', 2800.00, NULL, NULL, NULL);

-- ============================================================
-- 5. RELACIÓN REQUISITOS - STAKEHOLDERS (Trazabilidad origen)
-- ============================================================
INSERT INTO requisitos_stakeholders (requisito_id, stakeholder_id) VALUES
-- Proyecto 1 (ECO)
('88888888-8888-8888-8888-888888888801', '66666666-6666-6666-6666-666666666661'), -- Búsqueda -> CEO
('88888888-8888-8888-8888-888888888801', '66666666-6666-6666-6666-666666666664'), -- Búsqueda -> Cliente Final
('88888888-8888-8888-8888-888888888803', '66666666-6666-6666-6666-666666666661'), -- Pagos -> CEO
('88888888-8888-8888-8888-888888888804', '66666666-6666-6666-6666-666666666662'), -- Envíos -> Gte Logistica
('88888888-8888-8888-8888-888888888808', '66666666-6666-6666-6666-666666666663'), -- Seguridad -> Auditor

-- Proyecto 2 (HRM)
('88888888-8888-8888-8888-888888888811', '66666666-6666-6666-6666-666666666667'), -- Vacaciones -> Sindicato
('88888888-8888-8888-8888-888888888813', '66666666-6666-6666-6666-666666666665'), -- Nomina -> Dir RRHH
('88888888-8888-8888-8888-888888888814', '66666666-6666-6666-6666-666666666665'), -- GDPR -> Dir RRHH

-- Proyecto 4 (IA)
('88888888-8888-8888-8888-888888888816', '66666666-6666-6666-6666-666666666670'), -- Chatbot N1 -> Dir Operaciones
('88888888-8888-8888-8888-888888888818', '66666666-6666-6666-6666-666666666671'); -- Alucinacion -> Esp. IA

-- ============================================================
-- 6. DEPENDENCIAS ENTRE REQUISITOS (Precedencia)
-- ============================================================
INSERT INTO requisitos_dependencias (requisito_id, requisito_precedente_id) VALUES
-- Carrito (ECO-002) depende de Búsqueda (ECO-001) para encontrar los productos primero
('88888888-8888-8888-8888-888888888802', '88888888-8888-8888-8888-888888888801'),
-- Pago (ECO-003) depende del Carrito (ECO-002)
('88888888-8888-8888-8888-888888888803', '88888888-8888-8888-8888-888888888802'),
-- Factura (ECO-005) depende de Pago (ECO-003)
('88888888-8888-8888-8888-888888888805', '88888888-8888-8888-8888-888888888803'),
-- Aprobar vacaciones (HRM-002) depende de Solicitar (HRM-001)
('88888888-8888-8888-8888-888888888812', '88888888-8888-8888-8888-888888888811'),
-- Escalar a Humano (AI-002) depende de Chatbot N1 (AI-001)
('88888888-8888-8888-8888-888888888817', '88888888-8888-8888-8888-888888888816');

-- ============================================================
-- 7. CHECKLIST DE VALIDACIÓN (Peer Review)
-- ============================================================
INSERT INTO checklist_validacion (id, requisito_id, evaluador_id, es_atomico, es_testable, es_factible, no_ambiguo, es_completo, observaciones) VALUES
(gen_random_uuid(), '88888888-8888-8888-8888-888888888801', '33333333-3333-3333-3333-333333333333', true, true, true, true, true, 'El requisito de busqueda esta perfectamente redactado y es facil de probar.'),
(gen_random_uuid(), '88888888-8888-8888-8888-888888888811', '33333333-3333-3333-3333-333333333335', true, true, true, true, true, 'Solicitud de vacaciones clara.'),
(gen_random_uuid(), '88888888-8888-8888-8888-888888888816', '33333333-3333-3333-3333-333333333333', true, true, false, false, true, 'La integracion con OpenAI puede presentar latencias inesperadas. Factibilidad condicional a los limites de la API externa.');

-- ============================================================
-- 8. SOLICITUDES DE CAMBIO (Change Requests)
-- Simulación de cálculo Bayesiano: 
-- RF-ECO-001 (CRITICA, Alta, 2 stakeholders) -> Riesgo: ~50-60%
-- RNF-ECO-001 (ALTA, Sin Ceremonia, 1 stake) -> Riesgo: ~60-70%
-- ============================================================
INSERT INTO change_requests (id, requisito_id, solicitante_id, justificacion, impacto_tecnico, impacto_negocio, riesgos, esfuerzo_estimado, estado, probabilidad_riesgo, revisado_por_id) VALUES
(gen_random_uuid(), '88888888-8888-8888-8888-888888888801', '22222222-2222-2222-2222-222222222221', 'Se requiere agregar un filtro avanzado por inteligencia artificial visual (buscar por foto).', 'Requiere entrenar un modelo de vision artificial y reescribir la consulta de ElasticSearch.', 'Incrementa la conversion movil un 15%.', 'Alto riesgo de romper la busqueda actual.', '3 semanas', 'ANALIZADO', 65.40, '11111111-1111-1111-1111-111111111111'),
(gen_random_uuid(), '88888888-8888-8888-8888-888888888803', '11111111-1111-1111-1111-111111111111', 'El CEO solicita integrar Criptomonedas (Bitcoin) como metodo de pago adicional de urgencia.', 'Integracion con Coinbase API. Refactor de modelos contables.', 'Llegar a nicho tech.', 'Bugs en las conversiones de moneda.', '2 semanas', 'RECHAZADO', 85.50, '11111111-1111-1111-1111-111111111111'),
(gen_random_uuid(), '88888888-8888-8888-8888-888888888812', '22222222-2222-2222-2222-222222222222', 'El jefe directo tambien debe poder adjuntar un comentario obligatorio si rechaza las vacaciones.', 'Simple modificacion del modal y agregar columna string en BD.', 'Mejor comunicacion.', 'Ninguno', '4 horas', 'PROPUESTO', 32.10, NULL);

-- ============================================================
-- 9. HISTORIAL DE CAMBIOS (Logs de auditoría)
-- ============================================================
INSERT INTO requisitos_historial (id, requisito_id, usuario_id, tipo_cambio, campo_modificado, valor_anterior, valor_nuevo, descripcion_cambio) VALUES
(gen_random_uuid(), '88888888-8888-8888-8888-888888888801', '22222222-2222-2222-2222-222222222221', 'UPDATE', 'estado', 'EN_ANALISIS', 'VALIDADO', 'El requisito supero el peer review con exito.'),
(gen_random_uuid(), '88888888-8888-8888-8888-888888888801', '11111111-1111-1111-1111-111111111111', 'UPDATE', 'estado', 'VALIDADO', 'APROBADO', 'Aprobado formalmente por el jefe de proyecto. Listo para sprint 1.'),
(gen_random_uuid(), '88888888-8888-8888-8888-888888888811', '22222222-2222-2222-2222-222222222222', 'UPDATE', 'asignado_a_id', NULL, '33333333-3333-3333-3333-333333333334', 'Asignado al desarrollador para inicio de implementacion.');

-- FIN DEL SCRIPT MASIVO
