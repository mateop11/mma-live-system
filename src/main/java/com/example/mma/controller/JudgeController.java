package com.example.mma.controller;

import com.example.mma.builder.BoutDTOBuilder;
import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.*;
import com.example.mma.enums.*;
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
 * =============================================================================
 * CONTROLADOR DE JUECES - REFACTORIZADO CON SOLID Y PATRONES
 * =============================================================================
 * 
 * PRINCIPIO SOLID: DEPENDENCY INVERSION PRINCIPLE (DIP)
 * - Depende de IBoutService (abstracción) en lugar de repositorios directamente
 * - Facilita el testing y el cambio de implementación
 * 
 * PRINCIPIO SOLID: OPEN/CLOSED PRINCIPLE (OCP)
 * - La lógica de negocio está en el servicio, no aquí
 * - Nuevas funcionalidades se añaden en el servicio sin modificar el controller
 * 
 * PATRÓN DE DISEÑO: BUILDER PATTERN
 * - Usa BoutDTOBuilder para construir respuestas JSON
 * 
 * @author MMA Live System
 * @version 2.0 - Refactorizado con principios SOLID
 */
@RestController
@RequestMapping("/api/judge")
@PreAuthorize("hasAnyRole('ADMIN', 'JUDGE', 'SUPERVISOR')")
public class JudgeController {

    // DIP: Dependencia de interfaz, no de implementación concreta
    private final IBoutService boutService;
    private final BoutRepository boutRepository;
    private final ScoreCardRepository scoreCardRepository;
    private final UserRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * DIP: IBoutService es una abstracción que puede tener múltiples implementaciones.
     */
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

    /**
     * Obtiene todas las peleas asignadas.
     * 
     * BUILDER PATTERN: Usa BoutDTOBuilder para construir la respuesta.
     */
    @GetMapping("/bouts")
    public ResponseEntity<List<Map<String, Object>>> getAssignedBouts(Authentication auth) {
        // DIP: Usa el servicio en lugar del repositorio directamente
        List<Map<String, Object>> bouts = boutService.findAll().stream()
                .map(boutService::boutToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bouts);
    }

    /**
     * Obtiene los detalles de una pelea específica.
     */
    @GetMapping("/bouts/{id}")
    public ResponseEntity<Map<String, Object>> getBout(@PathVariable Long id) {
        // DIP: Delega al servicio
        return boutService.findById(id)
                .map(boutService::boutToDetailMap)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Inicia una pelea.
     * 
     * OCP: La lógica está en el servicio, el controller solo coordina.
     */
    @PostMapping("/bouts/{id}/start")
    public ResponseEntity<?> startBout(@PathVariable Long id) {
        // DIP + OCP: Delega toda la lógica al servicio
        return boutService.startBout(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Pausa una pelea en curso.
     */
    @PostMapping("/bouts/{id}/pause")
    public ResponseEntity<?> pauseBout(@PathVariable Long id) {
        return boutService.pauseBout(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Reanuda una pelea pausada.
     */
    @PostMapping("/bouts/{id}/resume")
    public ResponseEntity<?> resumeBout(@PathVariable Long id) {
        return boutService.resumeBout(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Avanza al siguiente round.
     */
    @PostMapping("/bouts/{id}/next-round")
    public ResponseEntity<?> nextRound(@PathVariable Long id) {
        return boutService.nextRound(id)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(Map.of("error", "Ya está en el último round")));
    }

    /**
     * Finaliza una pelea.
     * 
     * OCP: Los detalles de actualización de récords están en el servicio.
     */
    @PostMapping("/bouts/{id}/finish")
    public ResponseEntity<?> finishBout(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request
    ) {
        // Extraer parámetros del request
        Long winnerId = request.get("winnerId") != null 
                ? Long.valueOf(request.get("winnerId").toString()) : null;
        String decisionMethod = request.get("decisionMethod") != null 
                ? request.get("decisionMethod").toString() : null;
        String decisionType = request.get("decisionType") != null 
                ? request.get("decisionType").toString() : null;
        Integer finishRound = request.get("finishRound") != null 
                ? Integer.valueOf(request.get("finishRound").toString()) : null;

        // DIP: Delega al servicio toda la lógica de finalización
        return boutService.finishBout(id, winnerId, decisionMethod, decisionType, finishRound)
                .map(bout -> ResponseEntity.ok(boutService.boutToMap(bout)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Registra la puntuación de un juez para un round.
     * 
     * STRATEGY PATTERN: El servicio usa ScoringStrategy para validar puntuaciones.
     * DIP: Delega la lógica de guardado y notificación al servicio.
     */
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
            
            // DIP + STRATEGY: El servicio valida y guarda usando la estrategia configurada
            ScoreCard saved = boutService.submitScore(boutId, judge, roundNumber, 
                    fighter1Score, fighter2Score, notes);
            
            return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "roundNumber", saved.getRoundNumber(),
                "fighter1Score", saved.getFighter1Score(),
                "fighter2Score", saved.getFighter2Score()
            ));
        } catch (IllegalArgumentException e) {
            // Error de validación de la estrategia de puntuación
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene todas las puntuaciones de una pelea.
     */
    @GetMapping("/bouts/{boutId}/scores")
    public ResponseEntity<List<Map<String, Object>>> getBoutScores(@PathVariable Long boutId) {
        // DIP: Usa el servicio para obtener las puntuaciones
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

    /**
     * Calcula el resultado final de una pelea.
     * 
     * STRATEGY PATTERN: El cálculo se delega a la estrategia de puntuación.
     */
    @GetMapping("/bouts/{boutId}/calculate-result")
    public ResponseEntity<Map<String, Object>> calculateResult(@PathVariable Long boutId) {
        // STRATEGY PATTERN: El servicio usa ScoringStrategy internamente
        Map<String, Object> result = boutService.calculateFinalScore(boutId);
        return ResponseEntity.ok(result);
    }

    // ==================== MÉTODOS HELPER ELIMINADOS ====================
    // Los métodos boutToMap y boutToDetailMap han sido movidos a BoutDTOBuilder
    // siguiendo el patrón Builder y el principio DRY (Don't Repeat Yourself)
}

