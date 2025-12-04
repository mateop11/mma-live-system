package com.example.mma.controller;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.*;
import com.example.mma.enums.*;
import com.example.mma.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FighterRepository fighterRepository;
    private final BoutRepository boutRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RuleSetRepository ruleSetRepository;
    private final PasswordEncoder passwordEncoder;
    private final SimpMessagingTemplate messagingTemplate;

    public AdminController(
            FighterRepository fighterRepository,
            BoutRepository boutRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            RuleSetRepository ruleSetRepository,
            PasswordEncoder passwordEncoder,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.fighterRepository = fighterRepository;
        this.boutRepository = boutRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ruleSetRepository = ruleSetRepository;
        this.passwordEncoder = passwordEncoder;
        this.messagingTemplate = messagingTemplate;
    }

    // ==================== FIGHTERS ====================
    @GetMapping("/fighters")
    public ResponseEntity<List<FighterDTO>> getAllFighters() {
        return ResponseEntity.ok(fighterRepository.findAll().stream()
                .map(FighterDTO::new).collect(Collectors.toList()));
    }

    @PostMapping("/fighters")
    public ResponseEntity<FighterDTO> createFighter(@RequestBody FighterDTO dto) {
        Fighter fighter = new Fighter();
        fighter.setFirstName(dto.getFirstName());
        fighter.setLastName(dto.getLastName());
        fighter.setClub(dto.getClub());
        fighter.setCategoryWeight(WeightCategory.valueOf(dto.getCategoryWeight()));
        fighter.setStatus(dto.getStatus() != null ? FighterStatus.valueOf(dto.getStatus()) : FighterStatus.Active);
        fighter.setRecordW(dto.getRecordW() != null ? dto.getRecordW() : 0);
        fighter.setRecordL(dto.getRecordL() != null ? dto.getRecordL() : 0);
        fighter.setRecordD(dto.getRecordD() != null ? dto.getRecordD() : 0);
        
        Fighter saved = fighterRepository.save(fighter);
        return ResponseEntity.ok(new FighterDTO(saved));
    }

    @PutMapping("/fighters/{id}")
    public ResponseEntity<FighterDTO> updateFighter(@PathVariable Long id, @RequestBody FighterDTO dto) {
        return fighterRepository.findById(id)
                .map(fighter -> {
                    if (dto.getFirstName() != null) fighter.setFirstName(dto.getFirstName());
                    if (dto.getLastName() != null) fighter.setLastName(dto.getLastName());
                    if (dto.getClub() != null) fighter.setClub(dto.getClub());
                    if (dto.getCategoryWeight() != null) fighter.setCategoryWeight(WeightCategory.valueOf(dto.getCategoryWeight()));
                    if (dto.getStatus() != null) fighter.setStatus(FighterStatus.valueOf(dto.getStatus()));
                    Fighter saved = fighterRepository.save(fighter);
                    return ResponseEntity.ok(new FighterDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/fighters/{id}")
    public ResponseEntity<Void> deleteFighter(@PathVariable Long id) {
        if (fighterRepository.existsById(id)) {
            fighterRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== BOUTS ====================
    @PostMapping("/bouts")
    public ResponseEntity<?> createBout(@RequestBody Map<String, Object> request) {
        try {
            Long fighter1Id = Long.valueOf(request.get("fighter1Id").toString());
            Long fighter2Id = Long.valueOf(request.get("fighter2Id").toString());
            Integer rounds = request.get("rounds") != null ? Integer.valueOf(request.get("rounds").toString()) : 3;
            Long eventId = request.get("eventId") != null ? Long.valueOf(request.get("eventId").toString()) : null;

            Fighter fighter1 = fighterRepository.findById(fighter1Id)
                    .orElseThrow(() -> new RuntimeException("Peleador 1 no encontrado"));
            Fighter fighter2 = fighterRepository.findById(fighter2Id)
                    .orElseThrow(() -> new RuntimeException("Peleador 2 no encontrado"));

            Bout bout = new Bout(fighter1, fighter2, rounds);
            bout.setState(BoutState.Programada);
            bout.setScheduledAt(LocalDateTime.now());

            if (eventId != null) {
                Event event = eventRepository.findById(eventId).orElse(null);
                bout.setEvent(event);
            }

            Bout saved = boutRepository.save(bout);
            
            messagingTemplate.convertAndSend("/topic/bouts", Map.of(
                "action", "created",
                "bout", boutToMap(saved)
            ));

            return ResponseEntity.ok(boutToMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/bouts/{id}")
    public ResponseEntity<?> updateBout(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return boutRepository.findById(id)
                .map(bout -> {
                    if (request.get("state") != null) {
                        bout.setState(BoutState.valueOf(request.get("state").toString()));
                    }
                    if (request.get("currentRound") != null) {
                        bout.setCurrentRound(Integer.valueOf(request.get("currentRound").toString()));
                    }
                    if (request.get("winnerId") != null) {
                        Long winnerId = Long.valueOf(request.get("winnerId").toString());
                        Fighter winner = fighterRepository.findById(winnerId).orElse(null);
                        bout.setWinner(winner);
                    }
                    if (request.get("decisionMethod") != null) {
                        bout.setDecisionMethod(DecisionMethod.valueOf(request.get("decisionMethod").toString()));
                    }
                    
                    Bout saved = boutRepository.save(bout);
                    
                    messagingTemplate.convertAndSend("/topic/bouts", Map.of(
                        "action", "updated",
                        "bout", boutToMap(saved)
                    ));
                    
                    return ResponseEntity.ok(boutToMap(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bouts/{id}")
    public ResponseEntity<Void> deleteBout(@PathVariable Long id) {
        if (boutRepository.existsById(id)) {
            boutRepository.deleteById(id);
            messagingTemplate.convertAndSend("/topic/bouts", Map.of("action", "deleted", "boutId", id));
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
        return map;
    }

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

