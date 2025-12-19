package com.example.mma.service;

import com.example.mma.service.interfaces.INotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * =============================================================================
 * IMPLEMENTACIÓN DEL SERVICIO DE NOTIFICACIONES
 * =============================================================================
 * 
 * PRINCIPIO SOLID: DEPENDENCY INVERSION PRINCIPLE (DIP)
 * - Esta clase implementa INotificationService
 * - Los controllers dependen de la interfaz, no de esta implementación
 * - Facilita cambiar el mecanismo de notificación (WebSocket, Push, etc.)
 * 
 * PATRÓN DE DISEÑO: OBSERVER PATTERN (Implícito)
 * - Actúa como el "Subject" que notifica a los "Observers" (clientes WebSocket)
 * - Los clientes se suscriben a tópicos y reciben actualizaciones automáticamente
 * 
 * @author MMA Live System
 * @version 2.0 - Refactorizado con principios SOLID
 */
@Service
public class NotificationServiceImpl implements INotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor con inyección de dependencias.
     * DIP: Depende de SimpMessagingTemplate (abstracción de Spring)
     */
    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // ==================== NOTIFICACIONES DE PELEAS ====================

    /**
     * {@inheritDoc}
     * 
     * Notifica cambios generales a todos los suscriptores del tópico /topic/bouts
     */
    @Override
    public void notifyBoutChange(String action, Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>(data);
        message.put("action", action);
        message.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/bouts", message);
    }

    /**
     * {@inheritDoc}
     * 
     * Notifica cambios específicos de una pelea a sus suscriptores
     */
    @Override
    public void notifyBoutUpdate(Long boutId, String action, Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>(data);
        message.put("action", action);
        message.put("boutId", boutId);
        message.put("timestamp", System.currentTimeMillis());
        
        // Notificar al tópico específico de la pelea
        messagingTemplate.convertAndSend("/topic/bout/" + boutId, message);
        
        // También notificar al tópico general
        messagingTemplate.convertAndSend("/topic/bouts", message);
    }

    /**
     * {@inheritDoc}
     * 
     * Notifica cuando un juez envía una puntuación
     */
    @Override
    public void notifyScoreSubmitted(Long boutId, Map<String, Object> scoreData) {
        Map<String, Object> message = new HashMap<>(scoreData);
        message.put("action", "score_submitted");
        message.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/bout/" + boutId + "/scores", message);
    }

    // ==================== NOTIFICACIONES DE TIMER ====================

    /**
     * {@inheritDoc}
     * 
     * Notifica el estado actual del cronómetro
     */
    @Override
    public void notifyTimerUpdate(Long boutId, Map<String, Object> timerData) {
        messagingTemplate.convertAndSend("/topic/bout/" + boutId + "/timer", timerData);
        messagingTemplate.convertAndSend("/topic/timers", timerData);
    }

    /**
     * {@inheritDoc}
     * 
     * Notifica el fin de un round
     */
    @Override
    public void notifyRoundEnd(Long boutId, int round, int totalRounds) {
        Map<String, Object> message = Map.of(
            "event", "ROUND_END",
            "boutId", boutId,
            "round", round,
            "totalRounds", totalRounds,
            "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSend("/topic/bout/" + boutId + "/timer", message);
    }

    // ==================== BROADCAST GENERAL ====================

    /**
     * {@inheritDoc}
     * 
     * Método genérico para enviar mensajes a cualquier tópico
     */
    @Override
    public void broadcast(String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }
}

