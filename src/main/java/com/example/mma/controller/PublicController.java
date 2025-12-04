package com.example.mma.controller;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.*;
import com.example.mma.enums.BoutState;
import com.example.mma.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final FighterRepository fighterRepository;
    private final BoutRepository boutRepository;
    private final EventRepository eventRepository;

    public PublicController(
            FighterRepository fighterRepository,
            BoutRepository boutRepository,
            EventRepository eventRepository
    ) {
        this.fighterRepository = fighterRepository;
        this.boutRepository = boutRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/fighters")
    public ResponseEntity<List<FighterDTO>> getAllFighters() {
        List<FighterDTO> fighters = fighterRepository.findAll().stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fighters);
    }

    @GetMapping("/fighters/{id}")
    public ResponseEntity<FighterDTO> getFighter(@PathVariable Long id) {
        return fighterRepository.findById(id)
                .map(f -> ResponseEntity.ok(new FighterDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/events")
    public ResponseEntity<List<Map<String, Object>>> getAllEvents() {
        List<Map<String, Object>> events = eventRepository.findByActiveTrueOrderByEventDateDesc()
                .stream()
                .map(this::eventToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> getEvent(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(e -> ResponseEntity.ok(eventToMap(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bouts")
    public ResponseEntity<List<Map<String, Object>>> getAllBouts() {
        List<Map<String, Object>> bouts = boutRepository.findAll().stream()
                .map(this::boutToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bouts);
    }

    @GetMapping("/bouts/{id}")
    public ResponseEntity<Map<String, Object>> getBout(@PathVariable Long id) {
        return boutRepository.findById(id)
                .map(b -> ResponseEntity.ok(boutToMap(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bouts/live")
    public ResponseEntity<List<Map<String, Object>>> getLiveBouts() {
        List<Map<String, Object>> bouts = boutRepository.findByState(BoutState.EnCurso).stream()
                .map(this::boutToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bouts);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFighters", fighterRepository.count());
        stats.put("totalBouts", boutRepository.count());
        stats.put("totalEvents", eventRepository.count());
        stats.put("liveBouts", boutRepository.findByState(BoutState.EnCurso).size());
        stats.put("scheduledBouts", boutRepository.findByState(BoutState.Programada).size());
        return ResponseEntity.ok(stats);
    }

    private Map<String, Object> eventToMap(Event e) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", e.getId());
        map.put("name", e.getName());
        map.put("description", e.getDescription());
        map.put("location", e.getLocation());
        map.put("eventDate", e.getEventDate() != null ? e.getEventDate().toString() : null);
        map.put("eventType", e.getEventType() != null ? e.getEventType().name() : null);
        map.put("ruleSet", e.getRuleSet() != null ? e.getRuleSet().getName() : null);
        return map;
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
        map.put("scheduledAt", b.getScheduledAt() != null ? b.getScheduledAt().toString() : null);
        map.put("eventId", b.getEvent() != null ? b.getEvent().getId() : null);
        map.put("eventName", b.getEvent() != null ? b.getEvent().getName() : null);
        return map;
    }
}

