package com.example.mma.service;

import com.example.mma.builder.BoutDTOBuilder;
import com.example.mma.entity.*;
import com.example.mma.enums.*;
import com.example.mma.factory.BoutFactory;
import com.example.mma.repository.*;
import com.example.mma.service.interfaces.IBoutService;
import com.example.mma.service.interfaces.IFighterService;
import com.example.mma.service.interfaces.INotificationService;
import com.example.mma.strategy.ScoringStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del servicio de peleas.
 * 
 * SOLID - Dependency Inversion Principle (DIP):
 * Depende de interfaces (IFighterService, INotificationService).
 * 
 * SOLID - Open/Closed Principle (OCP):
 * Usa ScoringStrategy para cálculos - extensible sin modificar.
 * 
 * Patrones utilizados:
 * - Strategy: ScoringStrategy para puntuación
 * - Factory: BoutFactory para creación
 * - Builder: BoutDTOBuilder para DTOs
 */
@Service
@Transactional
public class BoutServiceImpl implements IBoutService {

    private final BoutRepository boutRepository;
    private final FighterRepository fighterRepository;
    private final ScoreCardRepository scoreCardRepository;
    private final IFighterService fighterService;
    private final INotificationService notificationService;
    private final BoutFactory boutFactory;
    private final ScoringStrategy scoringStrategy;

    public BoutServiceImpl(
            BoutRepository boutRepository,
            FighterRepository fighterRepository,
            ScoreCardRepository scoreCardRepository,
            IFighterService fighterService,
            INotificationService notificationService,
            BoutFactory boutFactory,
            @Qualifier("tenPointMustStrategy") ScoringStrategy scoringStrategy
    ) {
        this.boutRepository = boutRepository;
        this.fighterRepository = fighterRepository;
        this.scoreCardRepository = scoreCardRepository;
        this.fighterService = fighterService;
        this.notificationService = notificationService;
        this.boutFactory = boutFactory;
        this.scoringStrategy = scoringStrategy;
    }

    @Override
    public List<Bout> findAll() {
        return boutRepository.findAll();
    }

    @Override
    public Optional<Bout> findById(Long id) {
        return boutRepository.findById(id);
    }

    @Override
    public Bout createBout(Fighter fighter1, Fighter fighter2, Integer rounds, Long eventId) {
        // Patrón Factory: delega la creación a BoutFactory
        Bout bout = boutFactory.createFullConfiguredBout(fighter1, fighter2, rounds, eventId, null);
        Bout saved = boutRepository.save(bout);
        notificationService.notifyBoutChange("created", boutToMap(saved));
        return saved;
    }

    @Override
    public boolean deleteBout(Long id) {
        if (boutRepository.existsById(id)) {
            boutRepository.deleteById(id);
            notificationService.notifyBoutChange("deleted", Map.of("boutId", id));
            return true;
        }
        return false;
    }

    @Override
    public Optional<Bout> startBout(Long boutId) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    bout.setState(BoutState.EnCurso);
                    bout.setCurrentRound(1);
                    bout.setStartedAt(LocalDateTime.now());
                    Bout saved = boutRepository.save(bout);
                    Map<String, Object> boutData = BoutDTOBuilder.from(saved).buildStandardView().build();
                    notificationService.notifyBoutUpdate(boutId, "started", boutData);
                    return saved;
                });
    }

    @Override
    public Optional<Bout> pauseBout(Long boutId) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    bout.setState(BoutState.Pausada);
                    Bout saved = boutRepository.save(bout);
                    notificationService.notifyBoutUpdate(boutId, "paused", boutToMap(saved));
                    return saved;
                });
    }

    @Override
    public Optional<Bout> resumeBout(Long boutId) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    bout.setState(BoutState.EnCurso);
                    Bout saved = boutRepository.save(bout);
                    notificationService.notifyBoutUpdate(boutId, "resumed", boutToMap(saved));
                    return saved;
                });
    }

    @Override
    public Optional<Bout> nextRound(Long boutId) {
        return boutRepository.findById(boutId)
                .filter(bout -> bout.getCurrentRound() < bout.getTotalRounds())
                .map(bout -> {
                    bout.setCurrentRound(bout.getCurrentRound() + 1);
                    Bout saved = boutRepository.save(bout);
                    Map<String, Object> data = boutToMap(saved);
                    data.put("round", saved.getCurrentRound());
                    notificationService.notifyBoutUpdate(boutId, "next_round", data);
                    return saved;
                });
    }

    @Override
    public Optional<Bout> finishBout(Long boutId, Long winnerId, String decisionMethod,
                                      String decisionType, Integer finishRound) {
        return boutRepository.findById(boutId)
                .map(bout -> {
                    bout.setState(BoutState.Finalizada);
                    bout.setFinishedAt(LocalDateTime.now());
                    
                    if (winnerId != null) {
                        Fighter winner = fighterRepository.findById(winnerId).orElse(null);
                        bout.setWinner(winner);
                        
                        if (winner != null) {
                            fighterService.addWin(winner.getId());
                            Fighter loser = winner.getId().equals(bout.getFighter1().getId())
                                    ? bout.getFighter2() : bout.getFighter1();
                            fighterService.addLoss(loser.getId());
                        }
                    }
                    
                    if (decisionMethod != null) bout.setDecisionMethod(DecisionMethod.valueOf(decisionMethod));
                    if (decisionType != null) bout.setDecisionType(DecisionType.valueOf(decisionType));
                    if (finishRound != null) bout.setFinishRound(finishRound);
                    
                    Bout saved = boutRepository.save(bout);
                    Map<String, Object> boutData = BoutDTOBuilder.from(saved).buildDetailView().build();
                    notificationService.notifyBoutUpdate(boutId, "finished", boutData);
                    return saved;
                });
    }

    @Override
    public ScoreCard submitScore(Long boutId, User judge, Integer roundNumber,
                                  Integer fighter1Score, Integer fighter2Score, String notes) {
        Bout bout = boutRepository.findById(boutId)
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
        
        // Patrón Strategy: valida usando la estrategia configurada
        if (!scoringStrategy.isValidScore(fighter1Score, fighter2Score)) {
            throw new IllegalArgumentException("Puntuación inválida según " + scoringStrategy.getSystemName());
        }
        
        ScoreCard scoreCard = scoreCardRepository
                .findByBoutAndJudgeAndRoundNumber(bout, judge, roundNumber)
                .orElse(new ScoreCard(bout, judge, roundNumber));
        
        scoreCard.setFighter1Score(fighter1Score);
        scoreCard.setFighter2Score(fighter2Score);
        if (notes != null) scoreCard.setNotes(notes);
        
        ScoreCard saved = scoreCardRepository.save(scoreCard);
        
        notificationService.notifyScoreSubmitted(boutId, Map.of(
                "judgeId", judge.getId(),
                "judgeName", judge.getFullName(),
                "roundNumber", roundNumber,
                "fighter1Score", fighter1Score,
                "fighter2Score", fighter2Score
        ));
        
        return saved;
    }

    @Override
    public List<ScoreCard> getScores(Long boutId) {
        return boutRepository.findById(boutId)
                .map(scoreCardRepository::findByBout)
                .orElse(List.of());
    }

    @Override
    public Map<String, Object> calculateFinalScore(Long boutId) {
        // Patrón Strategy: delega el cálculo a la estrategia
        List<ScoreCard> scores = getScores(boutId);
        return scoringStrategy.determineWinner(scores);
    }

    @Override
    public List<Bout> getLiveBouts() {
        return boutRepository.findByState(BoutState.EnCurso);
    }

    @Override
    public List<Bout> getScheduledBouts() {
        return boutRepository.findByState(BoutState.Programada);
    }

    @Override
    public Map<String, Object> boutToMap(Bout bout) {
        // Patrón Builder
        return BoutDTOBuilder.from(bout).buildStandardView().build();
    }

    @Override
    public Map<String, Object> boutToDetailMap(Bout bout) {
        return BoutDTOBuilder.from(bout).buildDetailView().build();
    }
}
