package com.example.mma.service;

import com.example.mma.enums.TimerState;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class TimerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentHashMap<Long, TimerInstance> activeTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public TimerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void startTimer(Long boutId, int durationSeconds, int currentRound, int totalRounds) {
        stopTimer(boutId); // Detener si ya existe
        
        TimerInstance timer = new TimerInstance(boutId, durationSeconds, currentRound, totalRounds);
        activeTimers.put(boutId, timer);
        
        timer.future = scheduler.scheduleAtFixedRate(() -> {
            TimerInstance t = activeTimers.get(boutId);
            if (t != null && t.state == TimerState.RUNNING) {
                t.remainingSeconds--;
                
                broadcastTimerUpdate(t);
                
                if (t.remainingSeconds <= 0) {
                    t.state = TimerState.STOPPED;
                    broadcastRoundEnd(t);
                    t.future.cancel(false);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        timer.state = TimerState.RUNNING;
        broadcastTimerUpdate(timer);
    }

    public void pauseTimer(Long boutId) {
        TimerInstance timer = activeTimers.get(boutId);
        if (timer != null) {
            timer.state = TimerState.PAUSED;
            broadcastTimerUpdate(timer);
        }
    }

    public void resumeTimer(Long boutId) {
        TimerInstance timer = activeTimers.get(boutId);
        if (timer != null && timer.state == TimerState.PAUSED) {
            timer.state = TimerState.RUNNING;
            broadcastTimerUpdate(timer);
        }
    }

    public void stopTimer(Long boutId) {
        TimerInstance timer = activeTimers.remove(boutId);
        if (timer != null && timer.future != null) {
            timer.future.cancel(false);
            timer.state = TimerState.STOPPED;
            broadcastTimerUpdate(timer);
        }
    }

    public void setRound(Long boutId, int round, int durationSeconds) {
        TimerInstance timer = activeTimers.get(boutId);
        if (timer != null) {
            timer.currentRound = round;
            timer.remainingSeconds = durationSeconds;
            timer.totalSeconds = durationSeconds;
            timer.state = TimerState.STOPPED;
            broadcastTimerUpdate(timer);
        }
    }

    public TimerInstance getTimer(Long boutId) {
        return activeTimers.get(boutId);
    }

    public Map<String, Object> getTimerState(Long boutId) {
        TimerInstance timer = activeTimers.get(boutId);
        if (timer == null) {
            return Map.of(
                "boutId", boutId,
                "state", "STOPPED",
                "remainingSeconds", 0,
                "currentRound", 0
            );
        }
        return timerToMap(timer);
    }

    private void broadcastTimerUpdate(TimerInstance timer) {
        messagingTemplate.convertAndSend("/topic/bout/" + timer.boutId + "/timer", timerToMap(timer));
        messagingTemplate.convertAndSend("/topic/timers", timerToMap(timer));
    }

    private void broadcastRoundEnd(TimerInstance timer) {
        messagingTemplate.convertAndSend("/topic/bout/" + timer.boutId + "/timer", Map.of(
            "event", "ROUND_END",
            "boutId", timer.boutId,
            "round", timer.currentRound,
            "totalRounds", timer.totalRounds
        ));
    }

    private Map<String, Object> timerToMap(TimerInstance t) {
        int minutes = t.remainingSeconds / 60;
        int seconds = t.remainingSeconds % 60;
        return Map.of(
            "boutId", t.boutId,
            "state", t.state.name(),
            "remainingSeconds", t.remainingSeconds,
            "totalSeconds", t.totalSeconds,
            "currentRound", t.currentRound,
            "totalRounds", t.totalRounds,
            "displayTime", String.format("%02d:%02d", minutes, seconds),
            "progressPercent", t.totalSeconds > 0 ? (int)((t.remainingSeconds * 100.0) / t.totalSeconds) : 0
        );
    }

    public static class TimerInstance {
        public Long boutId;
        public int remainingSeconds;
        public int totalSeconds;
        public int currentRound;
        public int totalRounds;
        public TimerState state = TimerState.STOPPED;
        public ScheduledFuture<?> future;

        public TimerInstance(Long boutId, int durationSeconds, int currentRound, int totalRounds) {
            this.boutId = boutId;
            this.remainingSeconds = durationSeconds;
            this.totalSeconds = durationSeconds;
            this.currentRound = currentRound;
            this.totalRounds = totalRounds;
        }
    }
}

