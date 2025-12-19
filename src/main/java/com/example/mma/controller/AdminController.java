package com.example.mma.controller;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.*;
import com.example.mma.enums.*;
import com.example.mma.repository.*;
import com.example.mma.service.interfaces.IBoutService;
import com.example.mma.service.interfaces.IFighterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador de administración.
 * 
 * SOLID - Dependency Inversion Principle (DIP):
 * Depende de IFighterService e IBoutService (abstracciones).
 * 
 * Patrones utilizados:
 * - Factory: creación de Bouts via BoutFactory (en el servicio)
 * - Builder: DTOs construidos con FighterDTOBuilder (en el servicio)
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final IFighterService fighterService;
    private final IBoutService boutService;
    private final FighterRepository fighterRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RuleSetRepository ruleSetRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            IFighterService fighterService,
            IBoutService boutService,
            FighterRepository fighterRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            RuleSetRepository ruleSetRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.fighterService = fighterService;
        this.boutService = boutService;
        this.fighterRepository = fighterRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ruleSetRepository = ruleSetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== FIGHTERS ====================
    
    @GetMapping("/fighters")
    public ResponseEntity<List<FighterDTO>> getAllFighters() {
        return ResponseEntity.ok(fighterService.findAll());
    }

    @PostMapping("/fighters")
    public ResponseEntity<FighterDTO> createFighter(@RequestBody FighterDTO dto) {
        FighterDTO created = fighterService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/fighters/{id}")
    public ResponseEntity<FighterDTO> updateFighter(@PathVariable Long id, @RequestBody FighterDTO dto) {
        return fighterService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/fighters/{id}")
    public ResponseEntity<Void> deleteFighter(@PathVariable Long id) {
        if (fighterService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== BOUTS ====================
    
    // Patrón Factory: el servicio usa BoutFactory para crear peleas
    @PostMapping("/bouts")
    public ResponseEntity<?> createBout(@RequestBody Map<String, Object> request) {
        try {
            Long fighter1Id = Long.valueOf(request.get("fighter1Id").toString());
            Long fighter2Id = Long.valueOf(request.get("fighter2Id").toString());
            Integer rounds = request.get("rounds") != null 
                    ? Integer.valueOf(request.get("rounds").toString()) : 3;
            Long eventId = request.get("eventId") != null 
                    ? Long.valueOf(request.get("eventId").toString()) : null;

            Fighter fighter1 = fighterService.findEntityById(fighter1Id)
                    .orElseThrow(() -> new RuntimeException("Peleador 1 no encontrado"));
            Fighter fighter2 = fighterService.findEntityById(fighter2Id)
                    .orElseThrow(() -> new RuntimeException("Peleador 2 no encontrado"));

            Bout saved = boutService.createBout(fighter1, fighter2, rounds, eventId);
            return ResponseEntity.ok(boutService.boutToMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/bouts/{id}")
    public ResponseEntity<?> updateBout(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return boutService.findById(id)
                .map(bout -> {
                    if (request.get("state") != null) {
                        bout.setState(BoutState.valueOf(request.get("state").toString()));
                    }
                    if (request.get("currentRound") != null) {
                        bout.setCurrentRound(Integer.valueOf(request.get("currentRound").toString()));
                    }
                    if (request.get("winnerId") != null) {
                        Long winnerId = Long.valueOf(request.get("winnerId").toString());
                        fighterService.findEntityById(winnerId).ifPresent(bout::setWinner);
                    }
                    if (request.get("decisionMethod") != null) {
                        bout.setDecisionMethod(DecisionMethod.valueOf(request.get("decisionMethod").toString()));
                    }
                    return ResponseEntity.ok(boutService.boutToMap(bout));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bouts/{id}")
    public ResponseEntity<Void> deleteBout(@PathVariable Long id) {
        if (boutService.deleteBout(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== USERS ====================
    
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(this::userToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/judges")
    public ResponseEntity<List<Map<String, Object>>> getJudges() {
        List<Map<String, Object>> judges = userRepository.findByRole(UserRole.JUDGE).stream()
                .map(this::userToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(judges);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            if (userRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe"));
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(request.get("password").toString()));
            user.setFullName(request.get("fullName").toString());
            user.setRole(UserRole.valueOf(request.get("role").toString()));
            if (request.get("email") != null) {
                user.setEmail(request.get("email").toString());
            }

            User saved = userRepository.save(user);
            return ResponseEntity.ok(userToMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== EVENTS ====================
    
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> request) {
        try {
            Event event = new Event();
            event.setName(request.get("name").toString());
            if (request.get("description") != null) event.setDescription(request.get("description").toString());
            if (request.get("location") != null) event.setLocation(request.get("location").toString());
            event.setEventDate(LocalDateTime.now().plusDays(7));
            event.setEventType(EventType.valueOf(request.getOrDefault("eventType", "Professional").toString()));

            Event saved = eventRepository.save(event);
            return ResponseEntity.ok(eventToMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== HELPERS ====================
    
    private Map<String, Object> userToMap(User u) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", u.getId());
        map.put("username", u.getUsername());
        map.put("fullName", u.getFullName());
        map.put("email", u.getEmail());
        map.put("role", u.getRole().name());
        map.put("active", u.isActive());
        return map;
    }

    private Map<String, Object> eventToMap(Event e) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", e.getId());
        map.put("name", e.getName());
        map.put("description", e.getDescription());
        map.put("location", e.getLocation());
        map.put("eventDate", e.getEventDate() != null ? e.getEventDate().toString() : null);
        map.put("eventType", e.getEventType() != null ? e.getEventType().name() : null);
        return map;
    }
}
