package com.example.mma.repository;

import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FighterRepository extends JpaRepository<Fighter, Long> {
    
    List<Fighter> findByStatus(FighterStatus status);
    
    List<Fighter> findByCategoryWeight(WeightCategory categoryWeight);
    
    List<Fighter> findByStatusAndCategoryWeight(FighterStatus status, WeightCategory categoryWeight);
    
    List<Fighter> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}

