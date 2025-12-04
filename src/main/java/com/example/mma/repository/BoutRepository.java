package com.example.mma.repository;

import com.example.mma.entity.Bout;
import com.example.mma.entity.Event;
import com.example.mma.entity.Fighter;
import com.example.mma.enums.BoutState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoutRepository extends JpaRepository<Bout, Long> {
    List<Bout> findByEvent(Event event);
    List<Bout> findByState(BoutState state);
    
    @Query("SELECT b FROM Bout b WHERE b.fighter1 = :fighter OR b.fighter2 = :fighter ORDER BY b.scheduledAt DESC")
    List<Bout> findByFighter(@Param("fighter") Fighter fighter);
    
    @Query("SELECT b FROM Bout b WHERE b.winner = :fighter")
    List<Bout> findByWinner(@Param("fighter") Fighter fighter);
    
    List<Bout> findByEventOrderByBoutNumberAsc(Event event);
    
    @Query("SELECT b FROM Bout b WHERE b.state = 'EnCurso'")
    List<Bout> findActiveBouts();
}

