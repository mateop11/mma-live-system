package com.example.mma.strategy;

import com.example.mma.entity.ScoreCard;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Estrategia de puntuación: Sistema 10-Point Must.
 * 
 * Patrón Strategy - Implementación concreta:
 * Sistema estándar de MMA/Boxing donde el ganador del round recibe 10 puntos.
 * 
 * SOLID - Open/Closed Principle (OCP):
 * Nueva estrategia añadida sin modificar código existente.
 */
@Component("tenPointMustStrategy")
public class TenPointMustScoringStrategy implements ScoringStrategy {

    private static final int MAX_SCORE = 10;
    private static final int MIN_SCORE = 7;

    @Override
    public int calculateTotalScore(List<ScoreCard> scoreCards, int fighterNumber) {
        return scoreCards.stream()
                .mapToInt(sc -> fighterNumber == 1 ? sc.getFighter1Score() : sc.getFighter2Score())
                .sum();
    }

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

        // Agrupar por juez para determinar tipo de decisión
        Map<Long, int[]> scoresByJudge = new HashMap<>();
        for (ScoreCard sc : scoreCards) {
            Long judgeId = sc.getJudge().getId();
            scoresByJudge.computeIfAbsent(judgeId, k -> new int[2]);
            scoresByJudge.get(judgeId)[0] += sc.getFighter1Score();
            scoresByJudge.get(judgeId)[1] += sc.getFighter2Score();
        }

        int fighter1Wins = 0;
        int fighter2Wins = 0;
        int draws = 0;

        for (int[] scores : scoresByJudge.values()) {
            if (scores[0] > scores[1]) fighter1Wins++;
            else if (scores[1] > scores[0]) fighter2Wins++;
            else draws++;
        }

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

    @Override
    public boolean isValidScore(int fighter1Score, int fighter2Score) {
        if (fighter1Score < MIN_SCORE || fighter1Score > MAX_SCORE) return false;
        if (fighter2Score < MIN_SCORE || fighter2Score > MAX_SCORE) return false;
        if (fighter1Score != MAX_SCORE && fighter2Score != MAX_SCORE) return false;
        return true;
    }

    @Override
    public String getSystemName() {
        return "10-Point Must System";
    }

    @Override
    public String getDescription() {
        return "Sistema estándar MMA/Boxing: ganador del round = 10 puntos, perdedor = 9 o menos.";
    }
}
