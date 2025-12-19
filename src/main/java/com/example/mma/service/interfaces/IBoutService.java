package com.example.mma.service.interfaces;

import com.example.mma.entity.Bout;
import com.example.mma.entity.Fighter;
import com.example.mma.entity.ScoreCard;
import com.example.mma.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * =============================================================================
 * PRINCIPIO SOLID: DEPENDENCY INVERSION PRINCIPLE (DIP)
 * =============================================================================
 * 
 * Interfaz que define el contrato para el servicio de peleas (Bouts).
 * 
 * DIP en acción:
 * - Los controllers (alto nivel) dependen de esta abstracción
 * - La implementación concreta (bajo nivel) implementa esta interfaz
 * - Facilita el testing con mocks y stubs
 * 
 * =============================================================================
 * PRINCIPIO SOLID: INTERFACE SEGREGATION PRINCIPLE (ISP)
 * =============================================================================
 * 
 * Esta interfaz está diseñada siguiendo ISP:
 * - Métodos agrupados por responsabilidad
 * - Clientes solo necesitan conocer los métodos que usan
 * - Interfaces pequeñas y cohesivas
 * 
 * @author MMA Live System
 * @version 2.0 - Refactorizado con principios SOLID
 */
public interface IBoutService {

    // ==================== OPERACIONES CRUD ====================
    
    /**
     * Obtiene todas las peleas del sistema
     * @return Lista de todas las peleas
     */
    List<Bout> findAll();

    /**
     * Busca una pelea por su ID
     * @param id Identificador de la pelea
     * @return Optional con la pelea si existe
     */
    Optional<Bout> findById(Long id);

    /**
     * Crea una nueva pelea usando el Factory Pattern
     * @param fighter1 Primer peleador
     * @param fighter2 Segundo peleador
     * @param rounds Número de rounds
     * @param eventId ID del evento (opcional)
     * @return Pelea creada
     */
    Bout createBout(Fighter fighter1, Fighter fighter2, Integer rounds, Long eventId);

    /**
     * Elimina una pelea
     * @param id ID de la pelea
     * @return true si se eliminó correctamente
     */
    boolean deleteBout(Long id);

    // ==================== CONTROL DE PELEA ====================
    
    /**
     * Inicia una pelea
     * @param boutId ID de la pelea
     * @return Pelea actualizada
     */
    Optional<Bout> startBout(Long boutId);

    /**
     * Pausa una pelea en curso
     * @param boutId ID de la pelea
     * @return Pelea actualizada
     */
    Optional<Bout> pauseBout(Long boutId);

    /**
     * Reanuda una pelea pausada
     * @param boutId ID de la pelea
     * @return Pelea actualizada
     */
    Optional<Bout> resumeBout(Long boutId);

    /**
     * Avanza al siguiente round
     * @param boutId ID de la pelea
     * @return Pelea actualizada, o empty si ya está en el último round
     */
    Optional<Bout> nextRound(Long boutId);

    /**
     * Finaliza una pelea
     * @param boutId ID de la pelea
     * @param winnerId ID del ganador (puede ser null para empate)
     * @param decisionMethod Método de decisión
     * @param decisionType Tipo de decisión
     * @param finishRound Round en que terminó
     * @return Pelea finalizada
     */
    Optional<Bout> finishBout(Long boutId, Long winnerId, String decisionMethod, 
                              String decisionType, Integer finishRound);

    // ==================== SISTEMA DE PUNTUACIÓN ====================
    
    /**
     * Registra la puntuación de un juez para un round
     * @param boutId ID de la pelea
     * @param judge Juez que puntúa
     * @param roundNumber Número del round
     * @param fighter1Score Puntos para peleador 1
     * @param fighter2Score Puntos para peleador 2
     * @param notes Notas del juez
     * @return ScoreCard guardada
     */
    ScoreCard submitScore(Long boutId, User judge, Integer roundNumber,
                          Integer fighter1Score, Integer fighter2Score, String notes);

    /**
     * Obtiene todas las puntuaciones de una pelea
     * @param boutId ID de la pelea
     * @return Lista de scorecards
     */
    List<ScoreCard> getScores(Long boutId);

    /**
     * Calcula el resultado final basado en las puntuaciones
     * @param boutId ID de la pelea
     * @return Mapa con el resultado calculado
     */
    Map<String, Object> calculateFinalScore(Long boutId);

    // ==================== CONSULTAS ESPECIALIZADAS ====================
    
    /**
     * Obtiene las peleas en vivo (en curso)
     * @return Lista de peleas activas
     */
    List<Bout> getLiveBouts();

    /**
     * Obtiene las peleas programadas
     * @return Lista de peleas pendientes
     */
    List<Bout> getScheduledBouts();

    /**
     * Convierte una pelea a formato Map para respuestas JSON
     * @param bout Pelea a convertir
     * @return Mapa con los datos de la pelea
     */
    Map<String, Object> boutToMap(Bout bout);

    /**
     * Convierte una pelea a formato Map detallado
     * @param bout Pelea a convertir
     * @return Mapa con todos los detalles de la pelea
     */
    Map<String, Object> boutToDetailMap(Bout bout);
}

