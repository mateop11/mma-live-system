package com.example.mma.service.interfaces;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;

import java.util.List;
import java.util.Optional;

// SOLID: Dependency Inversion Principle (DIP) - Los controllers dependen de esta abstracción
public interface IFighterService {

    List<FighterDTO> findAll();
    Optional<FighterDTO> findById(Long id);
    Optional<Fighter> findEntityById(Long id);
    List<FighterDTO> findByStatus(FighterStatus status);
    List<FighterDTO> findByCategory(WeightCategory category);
    List<FighterDTO> search(String query);

    FighterDTO create(FighterDTO dto);
    Optional<FighterDTO> update(Long id, FighterDTO dto);
    boolean delete(Long id);

    void addWin(Long fighterId);
    void addLoss(Long fighterId);
    void addDraw(Long fighter1Id, Long fighter2Id);
}
