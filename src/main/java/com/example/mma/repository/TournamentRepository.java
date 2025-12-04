package com.example.mma.repository;

import com.example.mma.entity.Tournament;
import com.example.mma.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByEvent(Event event);
    List<Tournament> findByCompletedFalse();
}

