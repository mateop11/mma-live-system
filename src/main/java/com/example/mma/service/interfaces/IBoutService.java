package com.example.mma.service.interfaces;

import com.example.mma.entity.Bout;
import com.example.mma.entity.Fighter;
import com.example.mma.entity.ScoreCard;
import com.example.mma.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// SOLID: Dependency Inversion Principle (DIP) + Interface Segregation Principle (ISP)
public interface IBoutService {

    // CRUD
    List<Bout> findAll();
    Optional<Bout> findById(Long id);
    Bout createBout(Fighter fighter1, Fighter fighter2, Integer rounds, Long eventId);
    boolean deleteBout(Long id);

    // Control de pelea
    Optional<Bout> startBout(Long boutId);
    Optional<Bout> pauseBout(Long boutId);
    Optional<Bout> resumeBout(Long boutId);
    Optional<Bout> nextRound(Long boutId);
    Optional<Bout> finishBout(Long boutId, Long winnerId, String decisionMethod, 
                              String decisionType, Integer finishRound);

    // Puntuación
    ScoreCard submitScore(Long boutId, User judge, Integer roundNumber,
                          Integer fighter1Score, Integer fighter2Score, String notes);
    List<ScoreCard> getScores(Long boutId);
    Map<String, Object> calculateFinalScore(Long boutId);

    // Consultas
    List<Bout> getLiveBouts();
    List<Bout> getScheduledBouts();
    Map<String, Object> boutToMap(Bout bout);
    Map<String, Object> boutToDetailMap(Bout bout);
}
