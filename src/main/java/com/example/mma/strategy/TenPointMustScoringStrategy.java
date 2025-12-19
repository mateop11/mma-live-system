package com.example.mma.strategy;

import com.example.mma.entity.ScoreCard;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =============================================================================
 * PATRÓN DE DISEÑO: STRATEGY PATTERN - Implementación Concreta
 * =============================================================================
 * 
 * Implementación del sistema de puntuación "10-Point Must System".
 * 
 * Este es el sistema estándar usado en MMA y Boxing:
 * - El ganador del round recibe 10 puntos
 * - El perdedor recibe 9 puntos (o menos si fue dominado)
 * - Un round 10-8 indica dominancia clara
 * - Un round 10-7 es raro, indica destrucción total
 * 
 * =============================================================================
 * PRINCIPIO SOLID: OPEN/CLOSED PRINCIPLE (OCP) - En práctica
 * =============================================================================
 * 
 * Esta clase está CERRADA para modificación pero el sistema está ABIERTO
 * para extensión. Si necesitamos un nuevo sistema de puntuación:
 * 
 * Ejemplo: Sistema de puntos por técnica
 * 1. Crear TechniqueBasedScoringStrategy implements ScoringStrategy
 * 2. Anotar con @Component
 * 3. El sistema automáticamente la detecta
 * 
 * @author MMA Live System
 * @version 2.0 - Implementación del patrón Strategy
 */
@Component("tenPointMustStrategy")
public class TenPointMustScoringStrategy implements ScoringStrategy {

    // Constantes del sistema 10-Point Must
    private static final int MAX_SCORE = 10;
    private static final int MIN_SCORE = 7;
    private static final int STANDARD_LOSER_SCORE = 9;

    /**
     * Calcula el puntaje total sumando los scores de todas las tarjetas
     * para el peleador especificado.
     * 
     * El sistema agrupa por juez y round, luego suma todos los puntos.
     */
    @Override
    public int calculateTotalScore(List<ScoreCard> scoreCards, int fighterNumber) {
        // SOLID: Este método tiene una única responsabilidad - calcular totales
        return scoreCards.stream()
                .mapToInt(sc -> fighterNumber == 1 ? sc.getFighter1Score() : sc.getFighter2Score())
                .sum();
    }

    /**
     * Determina el ganador analizando las puntuaciones de todos los jueces.
     * 
     * Lógica:
     * 1. Suma puntos totales de cada peleador
     * 2. Cuenta cuántos jueces dan victoria a cada uno
     * 3. Determina el tipo de decisión (unánime, dividida, mayoritaria)
     */
    @Override
    public Map<String, Object> determineWinner(List<ScoreCard> scoreCards) {
        Map<String, Object> result = new HashMap<>();
        
        if (scoreCards.isEmpty()) {
            result.put("status", "NO_SCORES");
            result.put("message", "No hay puntuaciones registradas");
            return result;
        }

        // Calcular totales
        int fighter1Total = calculateTotalScore(scoreCards, 1);
        int fighter2Total = calculateTotalScore(scoreCards, 2);

        // Agrupar por juez para determinar tipo de decisión
        Map<Long, int[]> scoresByJudge = new HashMap<>();
        for (ScoreCard sc : scoreCards) {
            Long judgeId = sc.getJudge().getId();
            scoresByJudge.computeIfAbsent(judgeId, k -> new int[2]);
            scoresByJudge.get(judgeId)[0] += sc.getFighter1Score();
            scoresByJudge.get(judgeId)[1] += sc.getFighter2Score();
        }

        // Contar votos de jueces
        int fighter1Wins = 0;
        int fighter2Wins = 0;
        int draws = 0;

        for (int[] scores : scoresByJudge.values()) {
            if (scores[0] > scores[1]) fighter1Wins++;
            else if (scores[1] > scores[0]) fighter2Wins++;
            else draws++;
        }

        // Determinar resultado
        int totalJudges = scoresByJudge.size();
        String decisionType;
        Integer winnerId = null;

        if (fighter1Wins == totalJudges) {
            decisionType = "UNANIMOUS";
            winnerId = 1;
        } else if (fighter2Wins == totalJudges) {
            decisionType = "UNANIMOUS";
            winnerId = 2;
        } else if (fighter1Wins > fighter2Wins) {
            decisionType = draws > 0 ? "MAJORITY" : "SPLIT";
            winnerId = 1;
        } else if (fighter2Wins > fighter1Wins) {
            decisionType = draws > 0 ? "MAJORITY" : "SPLIT";
            winnerId = 2;
        } else {
            decisionType = "DRAW";
        }

        result.put("fighter1Total", fighter1Total);
        result.put("fighter2Total", fighter2Total);
        result.put("fighter1JudgeWins", fighter1Wins);
        result.put("fighter2JudgeWins", fighter2Wins);
        result.put("draws", draws);
        result.put("decisionType", decisionType);
        result.put("winnerFighterNumber", winnerId);
        result.put("totalJudges", totalJudges);
        result.put("scoringSystem", getSystemName());

        return result;
    }

    /**
     * Valida que la puntuación cumpla las reglas del 10-Point Must System:
     * - Al menos un peleador debe tener 10 puntos
     * - Los puntajes deben estar entre 7 y 10
     * - No pueden ser iguales a menos que ambos sean 10 (round muy parejo)
     */
    @Override
    public boolean isValidScore(int fighter1Score, int fighter2Score) {
        // Verificar rango válido
        if (fighter1Score < MIN_SCORE || fighter1Score > MAX_SCORE) return false;
        if (fighter2Score < MIN_SCORE || fighter2Score > MAX_SCORE) return false;
        
        // Al menos uno debe tener 10 puntos (regla "Must")
        if (fighter1Score != MAX_SCORE && fighter2Score != MAX_SCORE) return false;
        
        return true;
    }

    @Override
    public String getSystemName() {
        return "10-Point Must System";
    }

    @Override
    public String getDescription() {
        return "Sistema estándar de MMA/Boxing donde el ganador del round recibe 10 puntos " +
               "y el perdedor 9 o menos. Un round 10-8 indica dominancia clara.";
    }
}

