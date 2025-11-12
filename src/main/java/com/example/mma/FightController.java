package com.example.mma;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/fights")
@CrossOrigin(origins = "*")
public class FightController {

    private final FightRepository fightRepo;
    private final FighterRepository fighterRepo;

    public FightController(FightRepository fightRepo, FighterRepository fighterRepo) {
        this.fightRepo = fightRepo;
        this.fighterRepo = fighterRepo;
    }

    @GetMapping
    public List<Fight> findAll() {
        return fightRepo.findAll();
    }

    @GetMapping("/{id}")
    public Fight findById(@PathVariable Long id) {
        return fightRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fight not found"));
    }

    @PostMapping
    public Fight create(@RequestBody FightRequest req) {
        Fighter f1 = fighterRepo.findById(req.getFighter1Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "fighter1Id not found"));
        Fighter f2 = fighterRepo.findById(req.getFighter2Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "fighter2Id not found"));

        Fighter winner = null;
        if (req.getWinnerId() != null) {
            winner = fighterRepo.findById(req.getWinnerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "winnerId not found"));
        }

        Fight fight = new Fight();
        fight.setFighter1(f1);
        fight.setFighter2(f2);
        fight.setWinner(winner);
        fight.setRounds(req.getRounds() != null ? req.getRounds() : 3);
        fight.setStatus(req.getStatus() != null ? req.getStatus() : "Scheduled");

        return fightRepo.save(fight);
    }

    @PutMapping("/{id}")
    public Fight update(@PathVariable Long id, @RequestBody FightRequest req) {
        Fight existing = fightRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fight not found"));

        Fighter f1 = fighterRepo.findById(req.getFighter1Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "fighter1Id not found"));
        Fighter f2 = fighterRepo.findById(req.getFighter2Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "fighter2Id not found"));

        Fighter winner = null;
        if (req.getWinnerId() != null) {
            winner = fighterRepo.findById(req.getWinnerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "winnerId not found"));
        }

        existing.setFighter1(f1);
        existing.setFighter2(f2);
        existing.setWinner(winner);
        existing.setRounds(req.getRounds() != null ? req.getRounds() : existing.getRounds());
        existing.setStatus(req.getStatus() != null ? req.getStatus() : existing.getStatus());

        return fightRepo.save(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!fightRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fight not found");
        }
        fightRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO de entrada
    public static class FightRequest {
        private Long fighter1Id;
        private Long fighter2Id;
        private Long winnerId;
        private Integer rounds;
        private String status;

        public Long getFighter1Id() { return fighter1Id; }
        public void setFighter1Id(Long fighter1Id) { this.fighter1Id = fighter1Id; }

        public Long getFighter2Id() { return fighter2Id; }
        public void setFighter2Id(Long fighter2Id) { this.fighter2Id = fighter2Id; }

        public Long getWinnerId() { return winnerId; }
        public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }

        public Integer getRounds() { return rounds; }
        public void setRounds(Integer rounds) { this.rounds = rounds; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
