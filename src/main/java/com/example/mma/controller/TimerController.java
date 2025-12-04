package com.example.mma.controller;

import com.example.mma.entity.Bout;
import com.example.mma.entity.RuleSet;
import com.example.mma.enums.BoutState;
import com.example.mma.repository.BoutRepository;
import com.example.mma.repository.RuleSetRepository;
import com.example.mma.service.TimerService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/timer")
public class TimerController {

    private final TimerService timerService;
    private final BoutRepository boutRepository;
    private final RuleSetRepository ruleSetRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int DEFAULT_ROUND_DURATION = 300; // 5 minutos

    public TimerController(
            TimerService timerService,
            BoutRepository boutRepository,
            RuleSetRepository ruleSetRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.timerService = timerService;
        this.boutRepository = boutRepository;
        this.ruleSetRepository = ruleSetRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/{boutId}")
    public ResponseEntity<Map<String, Object>> getTimerState(@PathVariable Long boutId) {
        return ResponseEntity.ok(timerService.getTimerState(boutId));
    }

    @PostMapping("/{boutId}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
    public ResponseEntity<?> startTimer(@PathVariable Long boutId) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    int duration = getRoundDuration(bout);
                    int currentRound = bout.getCurrentRound() != null ? bout.getCurrentRound() : 1;
                    int totalRounds = bout.getTotalRounds() != null ? bout.getTotalRounds() : 3;
                    
                    // Actualizar estado de la pelea
                    bout.setState(BoutState.EnCurso);
                    if (bout.getCurrentRound() == null || bout.getCurrentRound() == 0) {
                        bout.setCurrentRound(1);
                        currentRound = 1;
                    }
                    boutRepository.save(bout);
                    
                    timerService.startTimer(boutId, duration, currentRound, totalRounds);
                    
                    // Notificar cambio de estado
                    messagingTemplate.convertAndSend("/topic/bouts", Map.of(
                        "action", "timer_started",
                        "boutId", boutId,
                        "round", currentRound
                    ));
                    
                    return ResponseEntity.ok(timerService.getTimerState(boutId));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{boutId}/pause")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
    public ResponseEntity<?> pauseTimer(@PathVariable Long boutId) {
        timerService.pauseTimer(boutId);
        
        boutRepository.findById(boutId).ifPresent(bout -> {
            bout.setState(BoutState.Pausada);
            boutRepository.save(bout);
        });
        
        messagingTemplate.convertAndSend("/topic/bouts", Map.of(
            "action", "timer_paused",
            "boutId", boutId
        ));
        
        return ResponseEntity.ok(timerService.getTimerState(boutId));
    }

    @PostMapping("/{boutId}/resume")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
    public ResponseEntity<?> resumeTimer(@PathVariable Long boutId) {
        timerService.resumeTimer(boutId);
        
        boutRepository.findById(boutId).ifPresent(bout -> {
            bout.setState(BoutState.EnCurso);
            boutRepository.save(bout);
        });
        
        messagingTemplate.convertAndSend("/topic/bouts", Map.of(
            "action", "timer_resumed",
            "boutId", boutId
        ));
        
        return ResponseEntity.ok(timerService.getTimerState(boutId));
    }

    @PostMapping("/{boutId}/stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
    public ResponseEntity<?> stopTimer(@PathVariable Long boutId) {
        timerService.stopTimer(boutId);
        return ResponseEntity.ok(timerService.getTimerState(boutId));
    }

    @PostMapping("/{boutId}/next-round")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
    public ResponseEntity<?> nextRound(@PathVariable Long boutId) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    int currentRound = bout.getCurrentRound() != null ? bout.getCurrentRound() : 1;
                    int totalRounds = bout.getTotalRounds() != null ? bout.getTotalRounds() : 3;
                    
                    if (currentRound >= totalRounds) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Ya es el último round"));
                    }
                    
                    // Incrementar round
                    int newRound = currentRound + 1;
                    bout.setCurrentRound(newRound);
                    boutRepository.save(bout);
                    
                    // Iniciar timer para nuevo round
                    int duration = getRoundDuration(bout);
                    timerService.startTimer(boutId, duration, newRound, totalRounds);
                    
                    messagingTemplate.convertAndSend("/topic/bouts", Map.of(
                        "action", "next_round",
                        "boutId", boutId,
                        "round", newRound
                    ));
                    
                    return ResponseEntity.ok(timerService.getTimerState(boutId));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{boutId}/reset")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
    public ResponseEntity<?> resetRound(@PathVariable Long boutId) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    int duration = getRoundDuration(bout);
                    int currentRound = bout.getCurrentRound() != null ? bout.getCurrentRound() : 1;
                    int totalRounds = bout.getTotalRounds() != null ? bout.getTotalRounds() : 3;
                    
                    timerService.setRound(boutId, currentRound, duration);
                    
                    return ResponseEntity.ok(timerService.getTimerState(boutId));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private int getRoundDuration(Bout bout) {
        if (bout.getEvent() != null && bout.getEvent().getRuleSet() != null) {
            return bout.getEvent().getRuleSet().getRoundDurationSeconds();
        }
        // Buscar reglas por defecto
        return ruleSetRepository.findByName("UFC Professional")
                .map(RuleSet::getRoundDurationSeconds)
                .orElse(DEFAULT_ROUND_DURATION);
    }

    // WebSocket endpoints
    @MessageMapping("/timer/{boutId}/start")
    @SendTo("/topic/bout/{boutId}/timer")
    public Map<String, Object> wsStartTimer(@DestinationVariable Long boutId) {
        startTimer(boutId);
        return timerService.getTimerState(boutId);
    }

    @MessageMapping("/timer/{boutId}/pause")
    @SendTo("/topic/bout/{boutId}/timer")
    public Map<String, Object> wsPauseTimer(@DestinationVariable Long boutId) {
        timerService.pauseTimer(boutId);
        return timerService.getTimerState(boutId);
    }

    @MessageMapping("/timer/{boutId}/resume")
    @SendTo("/topic/bout/{boutId}/timer")
    public Map<String, Object> wsResumeTimer(@DestinationVariable Long boutId) {
        timerService.resumeTimer(boutId);
        return timerService.getTimerState(boutId);
    }
}

