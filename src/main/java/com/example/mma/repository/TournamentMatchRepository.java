package com.example.mma.repository;

import com.example.mma.entity.Tournament;
import com.example.mma.entity.TournamentMatch;
import com.example.mma.enums.StageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {
    List<TournamentMatch> findByTournament(Tournament tournament);
    List<TournamentMatch> findByTournamentAndStage(Tournament tournament, StageType stage);
    List<TournamentMatch> findByTournamentAndCompletedFalse(Tournament tournament);
}

