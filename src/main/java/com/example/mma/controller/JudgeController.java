package com.example.mma.controller;

import com.example.mma.entity.*;
import com.example.mma.repository.*;
import com.example.mma.service.interfaces.IBoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para operaciones de jueces.
 * 
 * SOLID - Dependency Inversion Principle (DIP):
 * Depende de IBoutService (abstracción), no de implementación concreta.
 * 
 * SOLID - Open/Closed Principle (OCP):
 * La lógica de negocio está en el servicio, extensible sin modificar el controller.
 */
@RestController
@RequestMapping("/api/judge")
@PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
public class JudgeController {

    private final IBoutService boutService;
    private final BoutRepository boutRepository;
    private final ScoreCardRepository scoreCardRepository;
    private final UserRepository userRepository;

    public JudgeController(
            IBoutService boutService,
            BoutRepository boutRepository,
            ScoreCardRepository scoreCardRepository,
            UserRepository userRepository
    ) {
        this.boutService = boutService;
        this.boutRepository = boutRepository;
        this.scoreCardRepository = scoreCardRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/bouts")
    public ResponseEntity<List<Map<String, Object>>> getAssignedBouts(Authentication auth) {
        List<Map<String, Object>> bouts = boutService.findAll().stream()
                .map(boutService::boutToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bouts);
    }

    @GetMapping("/bouts/{id}")
    public ResponseEntity<Map<String, Object>> getBout(@PathVariable Long id) {
        return boutService.findById(id)
                .map(boutService::boutToDetailMap)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/start")
    public ResponseEntity<?> startBout(@PathVariable Long id) {
        return boutService.startBout(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/pause")
    public ResponseEntity<?> pauseBout(@PathVariable Long id) {
        return boutService.pauseBout(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/resume")
    public ResponseEntity<?> resumeBout(@PathVariable Long id) {
        return boutService.resumeBout(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bouts/{id}/next-round")
    public ResponseEntity<?> nextRound(@PathVariable Long id) {
        return boutService.nextRound(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(Map.of("error", "Ya está en el último round")));
    }

    @PostMapping("/bouts/{id}/finish")
    public ResponseEntity<?> finishBout(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long winnerId = request.get("winnerId") != null 
                ? Long.valueOf(request.get("winnerId").toString()) : null;
        String decisionMethod = request.get("decisionMethod") != null 
                ? request.get("decisionMethod").toString() : null;
        String decisionType = request.get("decisionType") != null 
                ? request.get("decisionType").toString() : null;
        Integer finishRound = request.get("finishRound") != null 
                ? Integer.valueOf(request.get("finishRound").toString()) : null;

        return boutService.finishBout(id, winnerId, decisionMethod, decisionType, finishRound)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Patrón Strategy: el servicio usa ScoringStrategy para validar puntuaciones
    @PostMapping("/bouts/{boutId}/score")
    public ResponseEntity<?> submitScore(
            @PathVariable Long boutId,
            @RequestBody Map<String, Object> request,
            Authentication auth
    ) {
        try {
            User judge = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Juez no encontrado"));
            
            Integer roundNumber = Integer.valueOf(request.get("roundNumber").toString());
            Integer fighter1Score = Integer.valueOf(request.get("fighter1Score").toString());
            Integer fighter2Score = Integer.valueOf(request.get("fighter2Score").toString());
            String notes = request.get("notes") != null ? request.get("notes").toString() : null;
            
            ScoreCard saved = boutService.submitScore(boutId, judge, roundNumber, 
                    fighter1Score, fighter2Score, notes);
            
            return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "roundNumber", saved.getRoundNumber(),
                "fighter1Score", saved.getFighter1Score(),
                "fighter2Score", saved.getFighter2Score()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bouts/{boutId}/scores")
    public ResponseEntity<List<Map<String, Object>>> getBoutScores(@PathVariable Long boutId) {
        List<ScoreCard> scores = boutService.getScores(boutId);
        
        if (scores.isEmpty() && boutService.findById(boutId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Map<String, Object>> scoreMaps = scores.stream()
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
        
        return ResponseEntity.ok(scoreMaps);
    }

    // Patrón Strategy: el cálculo se delega a la estrategia de puntuación
    @GetMapping("/bouts/{boutId}/calculate-result")
    public ResponseEntity<Map<String, Object>> calculateResult(@PathVariable Long boutId) {
        Map<String, Object> result = boutService.calculateFinalScore(boutId);
        return ResponseEntity.ok(result);
    }
}
