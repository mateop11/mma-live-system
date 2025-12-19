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
 * Factory para creación de peleas.
 * 
 * Patrón Factory:
 * Centraliza la lógica de creación de objetos Bout.
 * 
 * SOLID - Open/Closed Principle (OCP):
 * Abierto para añadir nuevos métodos de creación sin modificar los existentes.
 */
@Component
public class BoutFactory {

    private final EventRepository eventRepository;

    public BoutFactory(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Crea una pelea estándar (3 rounds por defecto)
    public Bout createStandardBout(Fighter fighter1, Fighter fighter2, Integer totalRounds) {
        Bout bout = new Bout();
        bout.setFighter1(fighter1);
        bout.setFighter2(fighter2);
        bout.setTotalRounds(totalRounds != null ? totalRounds : 3);
        bout.setState(BoutState.Programada);
        bout.setCurrentRound(0);
        bout.setScheduledAt(LocalDateTime.now());
        return bout;
    }

    // Crea una pelea de campeonato (5 rounds)
    public Bout createChampionshipBout(Fighter fighter1, Fighter fighter2, Long eventId) {
        Bout bout = createStandardBout(fighter1, fighter2, 5);
        if (eventId != null) {
            Event event = eventRepository.findById(eventId).orElse(null);
            bout.setEvent(event);
        }
        return bout;
    }

    // Crea una pelea de exhibición
    public Bout createExhibitionBout(Fighter fighter1, Fighter fighter2, Integer rounds) {
        return createStandardBout(fighter1, fighter2, rounds != null ? rounds : 3);
    }

    // Crea una pelea con configuración completa
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

    // Crea una pelea con horario específico
    public Bout createScheduledBout(Fighter fighter1, Fighter fighter2, 
                                     Integer totalRounds, LocalDateTime scheduledTime) {
        Bout bout = createStandardBout(fighter1, fighter2, totalRounds);
        bout.setScheduledAt(scheduledTime);
        return bout;
    }

    public Bout assignBoutNumber(Bout bout, Integer boutNumber) {
        bout.setBoutNumber(boutNumber);
        return bout;
    }
}
