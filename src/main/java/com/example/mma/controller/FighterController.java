package com.example.mma.controller;

import com.example.mma.dto.FighterDTO;
import com.example.mma.service.FighterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fighters")
public class FighterController {

    private final FighterService fighterService;

    @Autowired
    public FighterController(FighterService fighterService) {
        this.fighterService = fighterService;
    }

    @GetMapping
    public ResponseEntity<List<FighterDTO>> getAll() {
        return ResponseEntity.ok(fighterService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FighterDTO> getById(@PathVariable Long id) {
        return fighterService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FighterDTO> create(@RequestBody FighterDTO dto) {
        try {
            FighterDTO created = fighterService.create(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FighterDTO> update(@PathVariable Long id, @RequestBody FighterDTO dto) {
        return fighterService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (fighterService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<FighterDTO>> search(@RequestParam String q) {
        return ResponseEntity.ok(fighterService.search(q));
    }
}
