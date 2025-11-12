package com.example.mma;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/fighters")
@CrossOrigin(origins = "*")
public class FighterController {

    private final FighterRepository repository;

    public FighterController(FighterRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Fighter> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Fighter findById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fighter not found"));
    }

    @PostMapping
    public Fighter create(@RequestBody Fighter fighter) {
        fighter.setId(null);
        if (fighter.getRecordW() == null) fighter.setRecordW(0);
        if (fighter.getRecordL() == null) fighter.setRecordL(0);
        if (fighter.getRecordD() == null) fighter.setRecordD(0);
        if (fighter.getStatus() == null || fighter.getStatus().isBlank()) fighter.setStatus("Active");
        return repository.save(fighter);
    }

    @PutMapping("/{id}")
    public Fighter update(@PathVariable Long id, @RequestBody Fighter dto) {
        Fighter existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fighter not found"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setClub(dto.getClub());
        existing.setCategoryWeight(dto.getCategoryWeight());
        existing.setStatus(dto.getStatus());
        existing.setRecordW(dto.getRecordW() != null ? dto.getRecordW() : existing.getRecordW());
        existing.setRecordL(dto.getRecordL() != null ? dto.getRecordL() : existing.getRecordL());
        existing.setRecordD(dto.getRecordD() != null ? dto.getRecordD() : existing.getRecordD());

        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fighter not found");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
