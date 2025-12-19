package com.example.mma.factory;

import com.example.mma.entity.Bout;
import com.example.mma.entity.Event;
import com.example.mma.entity.Fighter;
import com.example.mma.entity.User;
import com.example.mma.enums.BoutState;
import com.example.mma.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * =============================================================================
 * PATRÓN DE DISEÑO: FACTORY PATTERN
 * =============================================================================
 * 
 * El patrón Factory proporciona una interfaz para crear objetos en una superclase,
 * pero permite a las subclases alterar el tipo de objetos que se crearán.
 * 
 * En este contexto:
 * - Centraliza la lógica de creación de peleas (Bouts)
 * - Encapsula la complejidad de inicialización
 * - Garantiza que todas las peleas se creen de forma consistente
 * 
 * Beneficios:
 * - Código de creación en un solo lugar
 * - Fácil de modificar la lógica de creación
 * - Los clientes no necesitan conocer los detalles de construcción
 * 
 * =============================================================================
 * PRINCIPIO SOLID: OPEN/CLOSED PRINCIPLE (OCP)
 * =============================================================================
 * 
 * Esta factory está ABIERTA para extensión:
 * - Se pueden añadir nuevos métodos para crear tipos especiales de peleas
 * - Ejemplo: createChampionshipBout(), createExhibitionBout()
 * 
 * Y CERRADA para modificación:
 * - Los métodos existentes no necesitan cambiar para añadir funcionalidad
 * 
 * @author MMA Live System
 * @version 2.0 - Implementación del patrón Factory
 */
@Component
public class BoutFactory {

    private final EventRepository eventRepository;

    public BoutFactory(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Crea una pelea estándar con la configuración por defecto.
     * 
     * @param fighter1 Primer peleador
     * @param fighter2 Segundo peleador
     * @param totalRounds Número de rounds (por defecto 3)
     * @return Nueva pelea configurada
     */
    public Bout createStandardBout(Fighter fighter1, Fighter fighter2, Integer totalRounds) {
        // Factory Method: Centraliza la creación y configuración inicial
        Bout bout = new Bout();
        
        bout.setFighter1(fighter1);
        bout.setFighter2(fighter2);
        bout.setTotalRounds(totalRounds != null ? totalRounds : 3);
        bout.setState(BoutState.Programada);
        bout.setCurrentRound(0);
        bout.setScheduledAt(LocalDateTime.now());
        
        return bout;
    }

    /**
     * Crea una pelea de campeonato (5 rounds).
     * 
     * @param fighter1 Primer peleador (retador o campeón)
     * @param fighter2 Segundo peleador
     * @param eventId ID del evento (requerido para campeonato)
     * @return Pelea de campeonato configurada
     */
    public Bout createChampionshipBout(Fighter fighter1, Fighter fighter2, Long eventId) {
        // OCP: Nuevo tipo de pelea sin modificar createStandardBout
        Bout bout = createStandardBout(fighter1, fighter2, 5); // Campeonatos son 5 rounds
        
        if (eventId != null) {
            Event event = eventRepository.findById(eventId).orElse(null);
            bout.setEvent(event);
        }
        
        return bout;
    }

    /**
     * Crea una pelea de exhibición (sin afectar récords).
     * 
     * @param fighter1 Primer peleador
     * @param fighter2 Segundo peleador
     * @param rounds Número de rounds
     * @return Pelea de exhibición
     */
    public Bout createExhibitionBout(Fighter fighter1, Fighter fighter2, Integer rounds) {
        // OCP: Otro tipo de pelea añadido sin modificar código existente
        Bout bout = createStandardBout(fighter1, fighter2, rounds != null ? rounds : 3);
        // Las peleas de exhibición podrían tener un flag especial en el futuro
        return bout;
    }

    /**
     * Crea una pelea con evento y jueces asignados.
     * 
     * @param fighter1 Primer peleador
     * @param fighter2 Segundo peleador
     * @param totalRounds Número de rounds
     * @param eventId ID del evento
     * @param judges Lista de jueces a asignar
     * @return Pelea completa con todas las configuraciones
     */
    public Bout createFullConfiguredBout(Fighter fighter1, Fighter fighter2, 
                                          Integer totalRounds, Long eventId, 
                                          List<User> judges) {
        Bout bout = createStandardBout(fighter1, fighter2, totalRounds);
        
        if (eventId != null) {
            Event event = eventRepository.findById(eventId).orElse(null);
            bout.setEvent(event);
        }
        
        if (judges != null && !judges.isEmpty()) {
            bout.setJudges(judges);
        }
        
        return bout;
    }

    /**
     * Crea una pelea con horario programado específico.
     * 
     * @param fighter1 Primer peleador
     * @param fighter2 Segundo peleador
     * @param totalRounds Número de rounds
     * @param scheduledTime Hora programada
     * @return Pelea programada
     */
    public Bout createScheduledBout(Fighter fighter1, Fighter fighter2, 
                                     Integer totalRounds, LocalDateTime scheduledTime) {
        Bout bout = createStandardBout(fighter1, fighter2, totalRounds);
        bout.setScheduledAt(scheduledTime);
        return bout;
    }

    /**
     * Asigna un número de pelea secuencial.
     * Útil para eventos con múltiples peleas.
     * 
     * @param bout Pelea a numerar
     * @param boutNumber Número asignado
     * @return Pelea con número asignado
     */
    public Bout assignBoutNumber(Bout bout, Integer boutNumber) {
        bout.setBoutNumber(boutNumber);
        return bout;
    }
}

