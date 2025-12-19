package com.example.mma.builder;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Bout;
import com.example.mma.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * =============================================================================
 * PATRÓN DE DISEÑO: BUILDER PATTERN
 * =============================================================================
 * 
 * El patrón Builder separa la construcción de un objeto complejo de su representación,
 * permitiendo crear diferentes representaciones con el mismo proceso de construcción.
 * 
 * En este contexto:
 * - Construye objetos Map<String, Object> para respuestas JSON
 * - Permite construir representaciones parciales o completas
 * - API fluida (method chaining) para mejor legibilidad
 * 
 * Beneficios:
 * - Código más legible que múltiples métodos con muchos parámetros
 * - Construcción paso a paso
 * - Inmutabilidad del resultado final
 * 
 * =============================================================================
 * PRINCIPIO SOLID: INTERFACE SEGREGATION PRINCIPLE (ISP)
 * =============================================================================
 * 
 * Este builder permite construir solo lo necesario:
 * - Para lista pública: solo datos básicos
 * - Para jueces: incluir puntuaciones
 * - Para admin: incluir todo
 * 
 * Los clientes solo "piden" los datos que necesitan.
 * 
 * @author MMA Live System
 * @version 2.0 - Implementación del patrón Builder
 */
public class BoutDTOBuilder {

    private final Map<String, Object> boutMap;
    private final Bout bout;

    /**
     * Constructor privado - usar from() para iniciar
     */
    private BoutDTOBuilder(Bout bout) {
        this.bout = bout;
        this.boutMap = new HashMap<>();
    }

    /**
     * Método factory para iniciar la construcción
     * 
     * @param bout Entidad Bout fuente
     * @return Builder inicializado
     */
    public static BoutDTOBuilder from(Bout bout) {
        return new BoutDTOBuilder(bout);
    }

    /**
     * Añade los datos básicos de la pelea
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withBasicInfo() {
        boutMap.put("id", bout.getId());
        boutMap.put("boutNumber", bout.getBoutNumber());
        boutMap.put("state", bout.getState() != null ? bout.getState().name() : null);
        boutMap.put("totalRounds", bout.getTotalRounds());
        boutMap.put("currentRound", bout.getCurrentRound());
        return this;
    }

    /**
     * Añade información de los peleadores
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withFighters() {
        boutMap.put("fighter1", bout.getFighter1() != null ? new FighterDTO(bout.getFighter1()) : null);
        boutMap.put("fighter2", bout.getFighter2() != null ? new FighterDTO(bout.getFighter2()) : null);
        return this;
    }

    /**
     * Añade información del ganador
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withWinner() {
        boutMap.put("winner", bout.getWinner() != null ? new FighterDTO(bout.getWinner()) : null);
        return this;
    }

    /**
     * Añade información del método de decisión
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withDecision() {
        boutMap.put("decisionMethod", bout.getDecisionMethod() != null ? bout.getDecisionMethod().name() : null);
        boutMap.put("decisionType", bout.getDecisionType() != null ? bout.getDecisionType().name() : null);
        boutMap.put("finishRound", bout.getFinishRound());
        boutMap.put("finishTimeSeconds", bout.getFinishTimeSeconds());
        return this;
    }

    /**
     * Añade timestamps de la pelea
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withTimestamps() {
        boutMap.put("scheduledAt", bout.getScheduledAt() != null ? bout.getScheduledAt().toString() : null);
        boutMap.put("startedAt", bout.getStartedAt() != null ? bout.getStartedAt().toString() : null);
        boutMap.put("finishedAt", bout.getFinishedAt() != null ? bout.getFinishedAt().toString() : null);
        return this;
    }

    /**
     * Añade lista de jueces asignados
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withJudges() {
        List<User> judges = bout.getJudges();
        if (judges != null) {
            boutMap.put("judges", judges.stream()
                    .map(j -> Map.of("id", j.getId(), "fullName", j.getFullName()))
                    .collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Añade información del evento
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withEvent() {
        if (bout.getEvent() != null) {
            boutMap.put("eventId", bout.getEvent().getId());
            boutMap.put("eventName", bout.getEvent().getName());
        }
        return this;
    }

    /**
     * Añade un campo personalizado
     * 
     * @param key Nombre del campo
     * @param value Valor del campo
     * @return this (para method chaining)
     */
    public BoutDTOBuilder withCustomField(String key, Object value) {
        boutMap.put(key, value);
        return this;
    }

    /**
     * Construye la vista básica (para listados públicos)
     * Combina: básico + peleadores
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder buildBasicView() {
        return this.withBasicInfo().withFighters();
    }

    /**
     * Construye la vista estándar (para listados internos)
     * Combina: básico + peleadores + ganador + decisión
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder buildStandardView() {
        return this.withBasicInfo().withFighters().withWinner().withDecision();
    }

    /**
     * Construye la vista detallada (para vista individual)
     * Incluye todos los datos
     * 
     * @return this (para method chaining)
     */
    public BoutDTOBuilder buildDetailView() {
        return this.withBasicInfo()
                   .withFighters()
                   .withWinner()
                   .withDecision()
                   .withTimestamps()
                   .withJudges()
                   .withEvent();
    }

    /**
     * Método terminal que retorna el Map construido
     * 
     * @return Map con los datos de la pelea
     */
    public Map<String, Object> build() {
        return new HashMap<>(boutMap); // Retorna copia para inmutabilidad
    }
}

