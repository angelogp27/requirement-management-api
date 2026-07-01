package pe.api.requirementmanagementapi.service;

import org.springframework.stereotype.Service;
import pe.api.requirementmanagementapi.model.Requisito;
import pe.api.requirementmanagementapi.model.enums.Prioridad;

import java.util.Map;

/**
 * Servicio de Inferencia Bayesiana para Prediccion de Riesgos.
 *
 * Implementa una Red Bayesiana Naive (sin dependencias externas)
 * para calcular P(Riesgo = Alto | Evidencia) usando el Teorema de Bayes.
 *
 * Red:
 *   [Prioridad] ---\
 *                    \
 *   [Ceremonia] -----> [RIESGO]
 *                    /
 *   [Stakeholders] /
 *
 * La inferencia se ejecuta en O(1), sin GPU ni hardware especial.
 */
@Service
public class BayesianInferenceService {

    // ===================================================================
    // TABLAS DE PROBABILIDAD CONDICIONAL (CPTs)
    // Estas tablas definen la "inteligencia" de la red.
    // En un sistema productivo se aprenden de datos historicos.
    // ===================================================================

    /**
     * P(Riesgo=Alto | Prioridad)
     * Cuanto mayor la prioridad del requisito afectado, mayor riesgo de impacto.
     */
    private static final Map<String, Double> CPT_PRIORIDAD = Map.of(
            "BAJA",    0.10,
            "MEDIA",   0.30,
            "ALTA",    0.65,
            "CRITICA", 0.85
    );

    /**
     * P(Riesgo=Alto | NivelCeremonia)
     * Si el requisito NO tiene caso de uso especificado (null), hay mas incertidumbre.
     * Si tiene alta ceremonia, esta mejor documentado y es menos riesgoso.
     */
    private static final Map<String, Double> CPT_CEREMONIA = Map.of(
            "ALTA",         0.25,   // Bien documentado -> menor riesgo
            "BAJA",         0.50,   // Documentacion basica -> riesgo moderado
            "SIN_ESPECIFICAR", 0.75 // Sin caso de uso -> alto riesgo
    );

    /**
     * P(Riesgo=Alto | CantidadStakeholders)
     * Mas stakeholders involucrados = mas personas afectadas = mas riesgo.
     */
    private static final Map<String, Double> CPT_STAKEHOLDERS = Map.of(
            "NINGUNO",   0.20,  // 0 stakeholders
            "POCOS",     0.40,  // 1-2 stakeholders
            "MUCHOS",    0.70   // 3+ stakeholders
    );

    /**
     * P(Riesgo=Alto) - Probabilidad a priori (prior)
     * Asumimos que en general, el 40% de los cambios son riesgosos.
     */
    private static final double PRIOR_RIESGO_ALTO = 0.40;

    /**
     * Calcula la probabilidad de riesgo de un cambio usando inferencia bayesiana.
     *
     * Aplica la formula de combinacion Naive Bayes:
     *   P(R|E1,E2,E3) ∝ P(R) * P(E1|R) * P(E2|R) * P(E3|R)
     *
     * El resultado se normaliza a un porcentaje (0-100).
     *
     * @param requisito El requisito afectado por la solicitud de cambio
     * @return Probabilidad de riesgo entre 0.0 y 100.0
     */
    public double calcularRiesgo(Requisito requisito) {
        // 1. Obtener evidencias del requisito
        double pPrioridad   = obtenerProbPrioridad(requisito.getPrioridad());
        double pCeremonia   = obtenerProbCeremonia(requisito.getNivelCeremonia());
        double pStakeholders = obtenerProbStakeholders(requisito.getStakeholders().size());

        // 2. Calcular P(Riesgo=Alto | Evidencia) usando Naive Bayes
        double pAlto = PRIOR_RIESGO_ALTO * pPrioridad * pCeremonia * pStakeholders;

        // 3. Calcular P(Riesgo=Bajo | Evidencia) para normalizar
        double pBajo = (1.0 - PRIOR_RIESGO_ALTO)
                * (1.0 - pPrioridad)
                * (1.0 - pCeremonia)
                * (1.0 - pStakeholders);

        // 4. Normalizar para obtener probabilidad entre 0 y 1
        double probabilidad = pAlto / (pAlto + pBajo);

        // 5. Convertir a porcentaje y redondear a 2 decimales
        return Math.round(probabilidad * 10000.0) / 100.0;
    }

    private double obtenerProbPrioridad(Prioridad prioridad) {
        if (prioridad == null) return CPT_PRIORIDAD.get("MEDIA");
        return CPT_PRIORIDAD.getOrDefault(prioridad.name(), 0.30);
    }

    private double obtenerProbCeremonia(String nivelCeremonia) {
        if (nivelCeremonia == null || nivelCeremonia.isBlank()) {
            return CPT_CEREMONIA.get("SIN_ESPECIFICAR");
        }
        return CPT_CEREMONIA.getOrDefault(nivelCeremonia, 0.50);
    }

    private double obtenerProbStakeholders(int cantidad) {
        if (cantidad == 0) return CPT_STAKEHOLDERS.get("NINGUNO");
        if (cantidad <= 2) return CPT_STAKEHOLDERS.get("POCOS");
        return CPT_STAKEHOLDERS.get("MUCHOS");
    }
}
