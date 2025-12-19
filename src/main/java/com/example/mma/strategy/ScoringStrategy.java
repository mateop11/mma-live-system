package com.example.mma.strategy;

import com.example.mma.entity.ScoreCard;
import java.util.List;
import java.util.Map;

/**
 * Interfaz para estrategias de puntuación.
 * 
 * Patrón Strategy:
 * Define una familia de algoritmos intercambiables para calcular puntuaciones.
 * 
 * SOLID - Open/Closed Principle (OCP):
 * Abierto para extensión (nuevas estrategias), cerrado para modificación.
 */
public interface ScoringStrategy {

    int calculateTotalScore(List<ScoreCard> scoreCards, int fighterNumber);
    Map<String, Object> determineWinner(List<ScoreCard> scoreCards);
    boolean isValidScore(int fighter1Score, int fighter2Score);
    String getSystemName();
    String getDescription();
}
