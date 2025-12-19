package com.example.mma.service.interfaces;

import java.util.Map;

/**
 * =============================================================================
 * PRINCIPIO SOLID: INTERFACE SEGREGATION PRINCIPLE (ISP)
 * =============================================================================
 * 
 * Esta interfaz segrega las operaciones de notificación del resto del sistema.
 * 
 * ISP establece que:
 * - Los clientes no deben depender de interfaces que no usan
 * - Es mejor tener muchas interfaces específicas que una general
 * 
 * Beneficios:
 * - Un servicio que solo necesita notificar no conoce detalles de puntuación
 * - Facilita el reemplazo del mecanismo de notificación (WebSocket, Push, Email)
 * - Desacopla la lógica de negocio del mecanismo de comunicación
 * 
 * =============================================================================
 * PATRÓN DE DISEÑO: OBSERVER PATTERN (Implícito)
 * =============================================================================
 * 
 * Esta interfaz actúa como el "Subject" del patrón Observer.
 * Los clientes WebSocket son los "Observers" que reciben las notificaciones.
 * 
 * @author MMA Live System
 * @version 2.0 - Refactorizado con principios SOLID y patrones de diseño
 */
public interface INotificationService {

    // ==================== NOTIFICACIONES DE PELEAS ====================
    
    /**
     * Notifica un cambio general en las peleas
     * @param action Acción realizada (created, updated, deleted, etc.)
     * @param data Datos del cambio
     */
    void notifyBoutChange(String action, Map<String, Object> data);

    /**
     * Notifica un cambio específico en una pelea
     * @param boutId ID de la pelea
     * @param action Acción realizada
     * @param data Datos del cambio
     */
    void notifyBoutUpdate(Long boutId, String action, Map<String, Object> data);

    /**
     * Notifica una nueva puntuación registrada
     * @param boutId ID de la pelea
     * @param scoreData Datos de la puntuación
     */
    void notifyScoreSubmitted(Long boutId, Map<String, Object> scoreData);

    // ==================== NOTIFICACIONES DE TIMER ====================
    
    /**
     * Notifica actualización del cronómetro
     * @param boutId ID de la pelea
     * @param timerData Estado actual del timer
     */
    void notifyTimerUpdate(Long boutId, Map<String, Object> timerData);

    /**
     * Notifica fin de round
     * @param boutId ID de la pelea
     * @param round Número del round que terminó
     * @param totalRounds Total de rounds
     */
    void notifyRoundEnd(Long boutId, int round, int totalRounds);

    // ==================== BROADCAST GENERAL ====================
    
    /**
     * Envía un mensaje a un tópico específico
     * @param topic Tópico de destino
     * @param payload Contenido del mensaje
     */
    void broadcast(String topic, Object payload);
}

