package com.example.mma.service;

import com.example.mma.service.interfaces.INotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de notificaciones.
 * 
 * SOLID - Dependency Inversion Principle (DIP):
 * Implementa INotificationService, permitiendo cambiar el mecanismo de notificación.
 * 
 * Patrón Observer:
 * Los clientes WebSocket reciben actualizaciones automáticamente.
 */
@Service
public class NotificationServiceImpl implements INotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notifyBoutChange(String action, Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>(data);
        message.put("action", action);
        message.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/bouts", message);
    }

    @Override
    public void notifyBoutUpdate(Long boutId, String action, Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>(data);
        message.put("action", action);
        message.put("boutId", boutId);
        message.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/bout/" + boutId, message);
        messagingTemplate.convertAndSend("/topic/bouts", message);
    }

    @Override
    public void notifyScoreSubmitted(Long boutId, Map<String, Object> scoreData) {
        Map<String, Object> message = new HashMap<>(scoreData);
        message.put("action", "score_submitted");
        message.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/bout/" + boutId + "/scores", message);
    }

    @Override
    public void notifyTimerUpdate(Long boutId, Map<String, Object> timerData) {
        messagingTemplate.convertAndSend("/topic/bout/" + boutId + "/timer", timerData);
        messagingTemplate.convertAndSend("/topic/timers", timerData);
    }

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

    @Override
    public void broadcast(String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }
}
