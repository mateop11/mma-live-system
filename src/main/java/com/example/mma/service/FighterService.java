package com.example.mma.service;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;
import com.example.mma.repository.FighterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FighterService {

    private final FighterRepository fighterRepository;

    @Autowired
    public FighterService(FighterRepository fighterRepository) {
        this.fighterRepository = fighterRepository;
    }

    public List<FighterDTO> findAll() {
        return fighterRepository.findAll().stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<FighterDTO> findById(Long id) {
        return fighterRepository.findById(id)
                .map(FighterDTO::new);
    }

    public Optional<Fighter> findEntityById(Long id) {
        return fighterRepository.findById(id);
    }

    public FighterDTO create(FighterDTO dto) {
        Fighter fighter = new Fighter();
        fighter.setFirstName(dto.getFirstName());
        fighter.setLastName(dto.getLastName());
        fighter.setClub(dto.getClub());
        
        if (dto.getCategoryWeight() != null) {
            fighter.setCategoryWeight(WeightCategory.valueOf(dto.getCategoryWeight()));
        }
        
        if (dto.getStatus() != null) {
            fighter.setStatus(FighterStatus.valueOf(dto.getStatus()));
        } else {
            fighter.setStatus(FighterStatus.Active);
        }
        
        fighter.setRecordW(dto.getRecordW() != null ? dto.getRecordW() : 0);
        fighter.setRecordL(dto.getRecordL() != null ? dto.getRecordL() : 0);
        fighter.setRecordD(dto.getRecordD() != null ? dto.getRecordD() : 0);

        Fighter saved = fighterRepository.save(fighter);
        return new FighterDTO(saved);
    }

    public Optional<FighterDTO> update(Long id, FighterDTO dto) {
        return fighterRepository.findById(id).map(fighter -> {
            if (dto.getFirstName() != null) {
                fighter.setFirstName(dto.getFirstName());
            }
            if (dto.getLastName() != null) {
                fighter.setLastName(dto.getLastName());
            }
            if (dto.getClub() != null) {
                fighter.setClub(dto.getClub());
            }
            if (dto.getCategoryWeight() != null) {
                fighter.setCategoryWeight(WeightCategory.valueOf(dto.getCategoryWeight()));
            }
            if (dto.getStatus() != null) {
                fighter.setStatus(FighterStatus.valueOf(dto.getStatus()));
            }
            if (dto.getRecordW() != null) {
                fighter.setRecordW(dto.getRecordW());
            }
            if (dto.getRecordL() != null) {
                fighter.setRecordL(dto.getRecordL());
            }
            if (dto.getRecordD() != null) {
                fighter.setRecordD(dto.getRecordD());
            }
            Fighter saved = fighterRepository.save(fighter);
            return new FighterDTO(saved);
        });
    }

    public boolean delete(Long id) {
        if (fighterRepository.existsById(id)) {
            fighterRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<FighterDTO> findByStatus(FighterStatus status) {
        return fighterRepository.findByStatus(status).stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    public List<FighterDTO> findByCategory(WeightCategory category) {
        return fighterRepository.findByCategoryWeight(category).stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    public List<FighterDTO> search(String query) {
        return fighterRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(FighterDTO::new)
                .collect(Collectors.toList());
    }

    public void addWin(Long fighterId) {
        fighterRepository.findById(fighterId).ifPresent(fighter -> {
            fighter.addWin();
            fighterRepository.save(fighter);
        });
    }

    public void addLoss(Long fighterId) {
        fighterRepository.findById(fighterId).ifPresent(fighter -> {
            fighter.addLoss();
            fighterRepository.save(fighter);
        });
    }

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

