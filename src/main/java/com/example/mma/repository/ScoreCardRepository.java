package com.example.mma.repository;

import com.example.mma.entity.Bout;
import com.example.mma.entity.ScoreCard;
import com.example.mma.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreCardRepository extends JpaRepository<ScoreCard, Long> {
    List<ScoreCard> findByBout(Bout bout);
    List<ScoreCard> findByBoutAndJudge(Bout bout, User judge);
    List<ScoreCard> findByBoutAndRoundNumber(Bout bout, Integer roundNumber);
    Optional<ScoreCard> findByBoutAndJudgeAndRoundNumber(Bout bout, User judge, Integer roundNumber);
}

