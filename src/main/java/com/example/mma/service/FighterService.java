package com.example.mma.service;

import com.example.mma.builder.FighterDTOBuilder;
import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;
import com.example.mma.repository.FighterRepository;
import com.example.mma.service.interfaces.IFighterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de peleadores.
 * 
 * SOLID - Dependency Inversion Principle (DIP):
 * Implementa IFighterService, los controllers dependen de la interfaz.
 * 
 * Patrón Builder:
 * Usa FighterDTOBuilder para crear DTOs.
 */
@Service
@Transactional
public class FighterService implements IFighterService {

    private final FighterRepository fighterRepository;

    @Autowired
    public FighterService(FighterRepository fighterRepository) {
        this.fighterRepository = fighterRepository;
    }

    @Override
    public List<FighterDTO> findAll() {
        return fighterRepository.findAll().stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FighterDTO> findById(Long id) {
        return fighterRepository.findById(id)
                .map(FighterDTO::new);
    }

    @Override
    public Optional<Fighter> findEntityById(Long id) {
        return fighterRepository.findById(id);
    }

    @Override
    public FighterDTO create(FighterDTO dto) {
        // Patrón Builder: construcción fluida
        Fighter fighter = FighterDTOBuilder.create()
                .withName(dto.getFirstName(), dto.getLastName())
                .withClub(dto.getClub())
                .withCategory(dto.getCategoryWeight())
                .withStatus(dto.getStatus() != null ? dto.getStatus() : "Active")
                .withRecord(dto.getRecordW(), dto.getRecordL(), dto.getRecordD())
                .buildEntity();

        Fighter saved = fighterRepository.save(fighter);
        return FighterDTOBuilder.from(saved).build();
    }

    @Override
    public Optional<FighterDTO> update(Long id, FighterDTO dto) {
        return fighterRepository.findById(id).map(fighter -> {
            if (dto.getFirstName() != null) fighter.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) fighter.setLastName(dto.getLastName());
            if (dto.getClub() != null) fighter.setClub(dto.getClub());
            if (dto.getCategoryWeight() != null) fighter.setCategoryWeight(WeightCategory.valueOf(dto.getCategoryWeight()));
            if (dto.getStatus() != null) fighter.setStatus(FighterStatus.valueOf(dto.getStatus()));
            if (dto.getRecordW() != null) fighter.setRecordW(dto.getRecordW());
            if (dto.getRecordL() != null) fighter.setRecordL(dto.getRecordL());
            if (dto.getRecordD() != null) fighter.setRecordD(dto.getRecordD());
            Fighter saved = fighterRepository.save(fighter);
            return FighterDTOBuilder.from(saved).build();
        });
    }

    @Override
    public boolean delete(Long id) {
        if (fighterRepository.existsById(id)) {
            fighterRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<FighterDTO> findByStatus(FighterStatus status) {
        return fighterRepository.findByStatus(status).stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<FighterDTO> findByCategory(WeightCategory category) {
        return fighterRepository.findByCategoryWeight(category).stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<FighterDTO> search(String query) {
        return fighterRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void addWin(Long fighterId) {
        fighterRepository.findById(fighterId).ifPresent(fighter -> {
            fighter.addWin();
            fighterRepository.save(fighter);
        });
    }

    @Override
    public void addLoss(Long fighterId) {
        fighterRepository.findById(fighterId).ifPresent(fighter -> {
            fighter.addLoss();
            fighterRepository.save(fighter);
        });
    }

    @Override
    public void addDraw(Long fighter1Id, Long fighter2Id) {
        fighterRepository.findById(fighter1Id).ifPresent(fighter -> {
            fighter.addDraw();
            fighterRepository.save(fighter);
        });
        fighterRepository.findById(fighter2Id).ifPresent(fighter -> {
            fighter.addDraw();
            fighterRepository.save(fighter);
        });
    }
}
