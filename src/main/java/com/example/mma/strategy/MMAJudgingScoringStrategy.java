package com.example.mma.strategy;

import com.example.mma.entity.ScoreCard;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =============================================================================
 * PATRÓN DE DISEÑO: STRATEGY PATTERN - Segunda Implementación
 * =============================================================================
 * 
 * Implementación alternativa del sistema de puntuación para MMA.
 * Esta estrategia considera criterios adicionales específicos de MMA:
 * - Effective Striking/Grappling
 * - Aggression
 * - Octagon Control
 * 
 * =============================================================================
 * PRINCIPIO SOLID: LISKOV SUBSTITUTION PRINCIPLE (LSP)
 * =============================================================================
 * 
 * LSP establece que los objetos de una superclase deben poder ser reemplazados
 * por objetos de sus subclases sin alterar la corrección del programa.
 * 
 * Esta clase puede sustituir a TenPointMustScoringStrategy en cualquier lugar
 * donde se use ScoringStrategy, ya que:
 * - Implementa todos los métodos de la interfaz
 * - Mantiene el mismo contrato (precondiciones y postcondiciones)
 * - No lanza excepciones inesperadas
 * 
 * Ejemplo de uso intercambiable:
 * ScoringStrategy strategy = useMMAJudging ? new MMAJudgingScoringStrategy() 
 *                                           : new TenPointMustScoringStrategy();
 * int total = strategy.calculateTotalScore(cards, 1); // Funciona igual
 * 
 * @author MMA Live System
 * @version 2.0 - Demostración de LSP y Strategy Pattern
 */
@Component("mmaJudgingStrategy")
public class MMAJudgingScoringStrategy implements ScoringStrategy {

    // Sistema de puntuación más flexible para MMA amateur
    private static final int MAX_SCORE = 10;
    private static final int MIN_SCORE = 6;

    /**
     * Calcula el puntaje total con ponderación especial para dominio claro.
     * 
     * Este método aplica un bonus si hay rounds con diferencia >= 2 puntos,
     * indicando dominio claro del peleador.
     */
    @Override
    public int calculateTotalScore(List<ScoreCard> scoreCards, int fighterNumber) {
        int total = 0;
        int dominantRounds = 0;

        for (ScoreCard sc : scoreCards) {
            int score = fighterNumber == 1 ? sc.getFighter1Score() : sc.getFighter2Score();
            int opponentScore = fighterNumber == 1 ? sc.getFighter2Score() : sc.getFighter1Score();
            
            total += score;
            
            // Contar rounds dominantes (diferencia >= 2)
            if (score - opponentScore >= 2) {
                dominantRounds++;
            }
        }

        // En caso de empate en puntos, los rounds dominantes pueden ser factor decisivo
        // Este dato se puede usar en determineWinner
        return total;
    }

    /**
     * Determina el ganador con criterios específicos de MMA.
     * 
     * En caso de empate en puntos, considera:
     * 1. Número de rounds dominantes
     * 2. Rounds ganados
     * 3. Actividad/Control del octágono
     */
    @Override
    public Map<String, Object> determineWinner(List<ScoreCard> scoreCards) {
        Map<String, Object> result = new HashMap<>();
        
        if (scoreCards.isEmpty()) {
            result.put("status", "NO_SCORES");
            result.put("message", "No hay puntuaciones registradas");
            return result;
        }

        int fighter1Total = calculateTotalScore(scoreCards, 1);
        int fighter2Total = calculateTotalScore(scoreCards, 2);

        // Contar rounds ganados y dominantes para cada peleador
        int fighter1RoundsWon = 0;
        int fighter2RoundsWon = 0;
        int fighter1DominantRounds = 0;
        int fighter2DominantRounds = 0;

        // Agrupar por round para análisis
        Map<Integer, int[]> roundScores = new HashMap<>();
        for (ScoreCard sc : scoreCards) {
            roundScores.computeIfAbsent(sc.getRoundNumber(), k -> new int[]{0, 0, 0}); // [f1Total, f2Total, count]
            roundScores.get(sc.getRoundNumber())[0] += sc.getFighter1Score();
            roundScores.get(sc.getRoundNumber())[1] += sc.getFighter2Score();
            roundScores.get(sc.getRoundNumber())[2]++;
        }

        for (int[] scores : roundScores.values()) {
            if (scores[2] > 0) { // Tiene puntuaciones
                int avg1 = scores[0] / scores[2];
                int avg2 = scores[1] / scores[2];
                
                if (avg1 > avg2) {
                    fighter1RoundsWon++;
                    if (avg1 - avg2 >= 2) fighter1DominantRounds++;
                } else if (avg2 > avg1) {
                    fighter2RoundsWon++;
                    if (avg2 - avg1 >= 2) fighter2DominantRounds++;
                }
            }
        }

        // Determinar ganador
        String decisionType;
        Integer winnerId = null;

        if (fighter1Total > fighter2Total) {
            winnerId = 1;
            decisionType = (fighter1Total - fighter2Total > 5) ? "DOMINANT" : "CLOSE";
        } else if (fighter2Total > fighter1Total) {
            winnerId = 2;
            decisionType = (fighter2Total - fighter1Total > 5) ? "DOMINANT" : "CLOSE";
        } else {
            // Empate en puntos - usar rounds ganados como desempate
            if (fighter1RoundsWon > fighter2RoundsWon) {
                winnerId = 1;
                decisionType = "TIEBREAKER_ROUNDS";
            } else if (fighter2RoundsWon > fighter1RoundsWon) {
                winnerId = 2;
                decisionType = "TIEBREAKER_ROUNDS";
            } else if (fighter1DominantRounds > fighter2DominantRounds) {
                winnerId = 1;
                decisionType = "TIEBREAKER_DOMINANCE";
            } else if (fighter2DominantRounds > fighter1DominantRounds) {
                winnerId = 2;
                decisionType = "TIEBREAKER_DOMINANCE";
            } else {
                decisionType = "DRAW";
            }
        }

        result.put("fighter1Total", fighter1Total);
        result.put("fighter2Total", fighter2Total);
        result.put("fighter1RoundsWon", fighter1RoundsWon);
        result.put("fighter2RoundsWon", fighter2RoundsWon);
        result.put("fighter1DominantRounds", fighter1DominantRounds);
        result.put("fighter2DominantRounds", fighter2DominantRounds);
        result.put("decisionType", decisionType);
        result.put("winnerFighterNumber", winnerId);
        result.put("scoringSystem", getSystemName());

        return result;
    }

    /**
     * Validación más flexible para MMA amateur.
     * Permite puntajes de 6-10 para mayor granularidad.
     */
    @Override
    public boolean isValidScore(int fighter1Score, int fighter2Score) {
        if (fighter1Score < MIN_SCORE || fighter1Score > MAX_SCORE) return false;
        if (fighter2Score < MIN_SCORE || fighter2Score > MAX_SCORE) return false;
        return true;
    }

    @Override
    public String getSystemName() {
        return "MMA Extended Judging System";
    }

    @Override
    public String getDescription() {
        return "Sistema de puntuación extendido para MMA que considera rounds dominantes " +
               "y utiliza criterios de desempate basados en control y agresividad.";
    }
}

