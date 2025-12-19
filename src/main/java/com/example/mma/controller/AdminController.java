package com.example.mma.controller;

import com.example.mma.builder.FighterDTOBuilder;
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
 * =============================================================================
 * CONTROLADOR DE ADMINISTRACIÓN - REFACTORIZADO CON SOLID Y PATRONES
 * =============================================================================
 * 
 * PRINCIPIO SOLID: DEPENDENCY INVERSION PRINCIPLE (DIP)
 * - Depende de IFighterService e IBoutService (abstracciones)
 * - No depende directamente de las implementaciones concretas
 * - Facilita el testing con mocks
 * 
 * PRINCIPIO SOLID: OPEN/CLOSED PRINCIPLE (OCP)
 * - La lógica de negocio está encapsulada en los servicios
 * - Nuevas funcionalidades se añaden en los servicios
 * - El controller solo coordina las operaciones
 * 
 * PATRÓN DE DISEÑO: BUILDER PATTERN
 * - Usa FighterDTOBuilder para construir DTOs de peleadores
 * 
 * PATRÓN DE DISEÑO: FACTORY PATTERN
 * - La creación de Bouts se delega al servicio que usa BoutFactory
 * 
 * @author MMA Live System
 * @version 2.0 - Refactorizado con principios SOLID y patrones de diseño
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    // DIP: Interfaces en lugar de implementaciones concretas
    private final IFighterService fighterService;
    private final IBoutService boutService;
    
    // Repositorios necesarios para operaciones no cubiertas por servicios
    private final FighterRepository fighterRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RuleSetRepository ruleSetRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias.
     * 
     * DIP en acción: Los servicios son inyectados como interfaces.
     */
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
    
    /**
     * Obtiene todos los peleadores.
     * 
     * DIP: Usa IFighterService en lugar del repositorio.
     */
    @GetMapping("/fighters")
    public ResponseEntity<List<FighterDTO>> getAllFighters() {
        // DIP: Delega al servicio
        return ResponseEntity.ok(fighterService.findAll());
    }

    /**
     * Crea un nuevo peleador.
     * 
     * DIP + BUILDER: El servicio usa FighterDTOBuilder internamente.
     */
    @PostMapping("/fighters")
    public ResponseEntity<FighterDTO> createFighter(@RequestBody FighterDTO dto) {
        // DIP: Delega la creación al servicio
        FighterDTO created = fighterService.create(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Actualiza un peleador existente.
     * 
     * DIP: Toda la lógica de actualización está en el servicio.
     */
    @PutMapping("/fighters/{id}")
    public ResponseEntity<FighterDTO> updateFighter(@PathVariable Long id, @RequestBody FighterDTO dto) {
        // DIP: Delega al servicio
        return fighterService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un peleador.
     */
    @DeleteMapping("/fighters/{id}")
    public ResponseEntity<Void> deleteFighter(@PathVariable Long id) {
        // DIP: Delega al servicio
        if (fighterService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== BOUTS ====================
    
    /**
     * Crea una nueva pelea.
     * 
     * FACTORY PATTERN: El servicio usa BoutFactory para crear la pelea.
     * DIP: Delega la creación al servicio.
     */
    @PostMapping("/bouts")
    public ResponseEntity<?> createBout(@RequestBody Map<String, Object> request) {
        try {
            Long fighter1Id = Long.valueOf(request.get("fighter1Id").toString());
            Long fighter2Id = Long.valueOf(request.get("fighter2Id").toString());
            Integer rounds = request.get("rounds") != null 
                    ? Integer.valueOf(request.get("rounds").toString()) : 3;
            Long eventId = request.get("eventId") != null 
                    ? Long.valueOf(request.get("eventId").toString()) : null;

            // Obtener peleadores usando el servicio (DIP)
            Fighter fighter1 = fighterService.findEntityById(fighter1Id)
                    .orElseThrow(() -> new RuntimeException("Peleador 1 no encontrado"));
            Fighter fighter2 = fighterService.findEntityById(fighter2Id)
                    .orElseThrow(() -> new RuntimeException("Peleador 2 no encontrado"));

            // FACTORY PATTERN: El servicio usa BoutFactory internamente
            Bout saved = boutService.createBout(fighter1, fighter2, rounds, eventId);

            return ResponseEntity.ok(boutService.boutToMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza una pelea existente.
     * 
     * Nota: Para operaciones complejas, se recomienda usar los endpoints
     * específicos del JudgeController (start, pause, finish, etc.)
     */
    @PutMapping("/bouts/{id}")
    public ResponseEntity<?> updateBout(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return boutService.findById(id)
                .map(bout -> {
                    // Actualización directa para campos simples
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
                    
                    // Nota: En una refactorización completa, esto debería moverse al servicio
                    return ResponseEntity.ok(boutService.boutToMap(bout));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina una pelea.
     * 
     * DIP: Delega al servicio que también maneja las notificaciones.
     */
    @DeleteMapping("/bouts/{id}")
    public ResponseEntity<Void> deleteBout(@PathVariable Long id) {
        // DIP: El servicio maneja la eliminación y notificación
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
    // Nota: boutToMap ha sido movido a IBoutService/BoutDTOBuilder
    // siguiendo el patrón Builder y DIP
    
    /**
     * Convierte un User a Map para respuestas JSON.
     * 
     * Nota: En una refactorización futura, esto podría moverse a un UserDTOBuilder.
     */
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

    /**
     * Convierte un Event a Map para respuestas JSON.
     * 
     * Nota: En una refactorización futura, esto podría moverse a un EventDTOBuilder.
     */
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

