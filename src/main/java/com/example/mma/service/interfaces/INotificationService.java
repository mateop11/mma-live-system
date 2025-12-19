package com.example.mma.service.interfaces;

import java.util.Map;

// SOLID: Interface Segregation Principle (ISP) + Patrón Observer
public interface INotificationService {

    void notifyBoutChange(String action, Map<String, Object> data);
    void notifyBoutUpdate(Long boutId, String action, Map<String, Object> data);
    void notifyScoreSubmitted(Long boutId, Map<String, Object> scoreData);
    void notifyTimerUpdate(Long boutId, Map<String, Object> timerData);
    void notifyRoundEnd(Long boutId, int round, int totalRounds);
    void broadcast(String topic, Object payload);
}
