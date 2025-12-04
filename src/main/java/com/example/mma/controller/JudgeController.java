package com.example.mma.controller;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.*;
import com.example.mma.enums.*;
import com.example.mma.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/judge")
@PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
public class JudgeController {

    private final BoutRepository boutRepository;
    private final FighterRepository fighterRepository;
    private final ScoreCardRepository scoreCardRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public JudgeController(
            BoutRepository boutRepository,
            FighterRepository fighterRepository,
            ScoreCardRepository scoreCardRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.boutRepository = boutRepository;
        this.fighterRepository = fighterRepository;
        this.scoreCardRepository = scoreCardRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/bouts")
    public ResponseEntity<List<Map<String, Object>>> getAssignedBouts(Authentication auth) {
        List<Map<String, Object>> bouts = boutRepository.findAll().stream()
                .map(this::boutToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bouts);
    }

    @GetMapping("/bouts/{id}")
    public ResponseEntity<Map<String, Object>> getBout(@PathVariable Long id) {
        return boutRepository.findById(id)
                .map(b -> ResponseEntity.ok(boutToDetailMap(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/start")
    public ResponseEntity<?> startBout(@PathVariable Long id) {
        return boutRepository.findById(id)
                .map(bout -> {
                    bout.setState(BoutState.EnCurso);
                    bout.setCurrentRound(1);
                    bout.setStartedAt(LocalDateTime.now());
                    Bout saved = boutRepository.save(bout);
                    
                    messagingTemplate.convertAndSend("/topic/bouts", Map.of(
                        "action", "started",
                        "bout", boutToMap(saved)
                    ));
                    messagingTemplate.convertAndSend("/topic/bout/" + id, Map.of(
                        "action", "started",
                        "bout", boutToMap(saved)
                    ));
                    
                    return ResponseEntity.ok(boutToMap(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/pause")
    public ResponseEntity<?> pauseBout(@PathVariable Long id) {
        return boutRepository.findById(id)
                .map(bout -> {
                    bout.setState(BoutState.Pausada);
                    Bout saved = boutRepository.save(bout);
                    
                    messagingTemplate.convertAndSend("/topic/bout/" + id, Map.of(
                        "action", "paused",
                        "bout", boutToMap(saved)
                    ));
                    
                    return ResponseEntity.ok(boutToMap(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/resume")
    public ResponseEntity<?> resumeBout(@PathVariable Long id) {
        return boutRepository.findById(id)
                .map(bout -> {
                    bout.setState(BoutState.EnCurso);
                    Bout saved = boutRepository.save(bout);
                    
                    messagingTemplate.convertAndSend("/topic/bout/" + id, Map.of(
                        "action", "resumed",
                        "bout", boutToMap(saved)
                    ));
                    
                    return ResponseEntity.ok(boutToMap(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/next-round")
    public ResponseEntity<?> nextRound(@PathVariable Long id) {
        return boutRepository.findById(id)
                .map(bout -> {
                    if (bout.getCurrentRound() < bout.getTotalRounds()) {
                        bout.setCurrentRound(bout.getCurrentRound() + 1);
                        Bout saved = boutRepository.save(bout);
                        
                        messagingTemplate.convertAndSend("/topic/bout/" + id, Map.of(
                            "action", "next_round",
                            "bout", boutToMap(saved),
                            "round", saved.getCurrentRound()
                        ));
                        
                        return ResponseEntity.ok(boutToMap(saved));
                    }
                    return ResponseEntity.badRequest().body(Map.of("error", "Ya está en el último round"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/finish")
    public ResponseEntity<?> finishBout(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request
    ) {
        return boutRepository.findById(id)
                .map(bout -> {
                    bout.setState(BoutState.Finalizada);
                    bout.setFinishedAt(LocalDateTime.now());
                    
                    if (request.get("winnerId") != null) {
                        Long winnerId = Long.valueOf(request.get("winnerId").toString());
                        Fighter winner = fighterRepository.findById(winnerId).orElse(null);
                        bout.setWinner(winner);
                        
                        // Actualizar records
                        if (winner != null) {
                            winner.addWin();
                            fighterRepository.save(winner);
                            
                            Fighter loser = winner.getId().equals(bout.getFighter1().getId()) 
                                    ? bout.getFighter2() 
                                    : bout.getFighter1();
                            loser.addLoss();
                            fighterRepository.save(loser);
                        }
                    }
                    
                    if (request.get("decisionMethod") != null) {
                        bout.setDecisionMethod(DecisionMethod.valueOf(request.get("decisionMethod").toString()));
                    }
                    
                    if (request.get("decisionType") != null) {
                        bout.setDecisionType(DecisionType.valueOf(request.get("decisionType").toString()));
                    }
                    
                    if (request.get("finishRound") != null) {
                        bout.setFinishRound(Integer.valueOf(request.get("finishRound").toString()));
                    }
                    
                    Bout saved = boutRepository.save(bout);
                    
                    messagingTemplate.convertAndSend("/topic/bouts", Map.of(
                        "action", "finished",
                        "bout", boutToMap(saved)
                    ));
                    messagingTemplate.convertAndSend("/topic/bout/" + id, Map.of(
                        "action", "finished",
                        "bout", boutToMap(saved)
                    ));
                    
                    return ResponseEntity.ok(boutToMap(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{boutId}/score")
    public ResponseEntity<?> submitScore(
            @PathVariable Long boutId,
            @RequestBody Map<String, Object> request,
            Authentication auth
    ) {
        try {
            Bout bout = boutRepository.findById(boutId)
                    .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
            
            User judge = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Juez no encontrado"));
            
            Integer roundNumber = Integer.valueOf(request.get("roundNumber").toString());
            Integer fighter1Score = Integer.valueOf(request.get("fighter1Score").toString());
            Integer fighter2Score = Integer.valueOf(request.get("fighter2Score").toString());
            
            ScoreCard scoreCard = scoreCardRepository
                    .findByBoutAndJudgeAndRoundNumber(bout, judge, roundNumber)
                    .orElse(new ScoreCard(bout, judge, roundNumber));
            
            scoreCard.setFighter1Score(fighter1Score);
            scoreCard.setFighter2Score(fighter2Score);
            if (request.get("notes") != null) {
                scoreCard.setNotes(request.get("notes").toString());
            }
            
            ScoreCard saved = scoreCardRepository.save(scoreCard);
            
            messagingTemplate.convertAndSend("/topic/bout/" + boutId + "/scores", Map.of(
                "action", "score_submitted",
                "judgeId", judge.getId(),
                "judgeName", judge.getFullName(),
                "roundNumber", roundNumber,
                "fighter1Score", fighter1Score,
                "fighter2Score", fighter2Score
            ));
            
            return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "roundNumber", saved.getRoundNumber(),
                "fighter1Score", saved.getFighter1Score(),
                "fighter2Score", saved.getFighter2Score()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bouts/{boutId}/scores")
    public ResponseEntity<List<Map<String, Object>>> getBoutScores(@PathVariable Long boutId) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    List<Map<String, Object>> scores = scoreCardRepository.findByBout(bout).stream()
                            .map(sc -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", sc.getId());
                                map.put("judgeId", sc.getJudge().getId());
                                map.put("judgeName", sc.getJudge().getFullName());
                                map.put("roundNumber", sc.getRoundNumber());
                                map.put("fighter1Score", sc.getFighter1Score());
                                map.put("fighter2Score", sc.getFighter2Score());
                                map.put("notes", sc.getNotes());
                                return map;
                            })
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(scores);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> boutToMap(Bout b) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", b.getId());
        map.put("boutNumber", b.getBoutNumber());
        map.put("fighter1", b.getFighter1() != null ? new FighterDTO(b.getFighter1()) : null);
        map.put("fighter2", b.getFighter2() != null ? new FighterDTO(b.getFighter2()) : null);
        map.put("winner", b.getWinner() != null ? new FighterDTO(b.getWinner()) : null);
        map.put("state", b.getState() != null ? b.getState().name() : null);
        map.put("totalRounds", b.getTotalRounds());
        map.put("currentRound", b.getCurrentRound());
        map.put("decisionMethod", b.getDecisionMethod() != null ? b.getDecisionMethod().name() : null);
        map.put("decisionType", b.getDecisionType() != null ? b.getDecisionType().name() : null);
        return map;
    }

    private Map<String, Object> boutToDetailMap(Bout b) {
        Map<String, Object> map = boutToMap(b);
        map.put("scheduledAt", b.getScheduledAt() != null ? b.getScheduledAt().toString() : null);
        map.put("startedAt", b.getStartedAt() != null ? b.getStartedAt().toString() : null);
        map.put("finishedAt", b.getFinishedAt() != null ? b.getFinishedAt().toString() : null);
        map.put("finishRound", b.getFinishRound());
        map.put("finishTimeSeconds", b.getFinishTimeSeconds());
        
        // Incluir jueces asignados
        map.put("judges", b.getJudges().stream()
                .map(j -> Map.of("id", j.getId(), "fullName", j.getFullName()))
                .collect(Collectors.toList()));
        
        return map;
    }
}

