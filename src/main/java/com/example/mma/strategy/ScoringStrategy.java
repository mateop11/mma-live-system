package com.example.mma.strategy;

import com.example.mma.entity.ScoreCard;
import java.util.List;
import java.util.Map;

/**
 * =============================================================================
 * PATRÓN DE DISEÑO: STRATEGY PATTERN
 * =============================================================================
 * 
 * El patrón Strategy define una familia de algoritmos, encapsula cada uno de ellos
 * y los hace intercambiables. Permite que el algoritmo varíe independientemente
 * de los clientes que lo utilizan.
 * 
 * En este contexto:
 * - Diferentes sistemas de puntuación (MMA, Boxing, etc.)
 * - Cada estrategia implementa su propia lógica de cálculo
 * - El sistema puede cambiar de estrategia en tiempo de ejecución
 * 
 * =============================================================================
 * PRINCIPIO SOLID: OPEN/CLOSED PRINCIPLE (OCP)
 * =============================================================================
 * 
 * OCP establece que el software debe estar:
 * - ABIERTO para extensión: Nuevas estrategias se añaden creando nuevas clases
 * - CERRADO para modificación: No se modifica el código existente
 * 
 * Para añadir un nuevo sistema de puntuación:
 * 1. Crear una nueva clase que implemente ScoringStrategy
 * 2. Registrarla en el contexto
 * 3. ¡Sin modificar código existente!
 * 
 * @author MMA Live System
 * @version 2.0 - Implementación del patrón Strategy
 */
public interface ScoringStrategy {

    /**
     * Calcula el puntaje total de un peleador basado en las tarjetas de puntuación
     * 
     * @param scoreCards Lista de tarjetas de puntuación
     * @param fighterNumber Número del peleador (1 o 2)
     * @return Puntaje total calculado
     */
    int calculateTotalScore(List<ScoreCard> scoreCards, int fighterNumber);

    /**
     * Determina el ganador basado en las puntuaciones
     * 
     * @param scoreCards Lista de tarjetas de puntuación
     * @return Mapa con el resultado: winnerId, method, scores
     */
    Map<String, Object> determineWinner(List<ScoreCard> scoreCards);

    /**
     * Valida si una puntuación es válida según las reglas del sistema
     * 
     * @param fighter1Score Puntuación del peleador 1
     * @param fighter2Score Puntuación del peleador 2
     * @return true si la puntuación es válida
     */
    boolean isValidScore(int fighter1Score, int fighter2Score);

    /**
     * Obtiene el nombre del sistema de puntuación
     * 
     * @return Nombre descriptivo del sistema
     */
    String getSystemName();

    /**
     * Obtiene la descripción del sistema de puntuación
     * 
     * @return Descripción de cómo funciona el sistema
     */
    String getDescription();
}

