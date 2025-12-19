package com.example.mma.builder;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Bout;
import com.example.mma.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Patrón Builder + SOLID: Interface Segregation Principle (ISP)
public class BoutDTOBuilder {

    private final Map<String, Object> boutMap;
    private final Bout bout;

    private BoutDTOBuilder(Bout bout) {
        this.bout = bout;
        this.boutMap = new HashMap<>();
    }

    public static BoutDTOBuilder from(Bout bout) {
        return new BoutDTOBuilder(bout);
    }

    public BoutDTOBuilder withBasicInfo() {
        boutMap.put("id", bout.getId());
        boutMap.put("boutNumber", bout.getBoutNumber());
        boutMap.put("state", bout.getState() != null ? bout.getState().name() : null);
        boutMap.put("totalRounds", bout.getTotalRounds());
        boutMap.put("currentRound", bout.getCurrentRound());
        return this;
    }

    public BoutDTOBuilder withFighters() {
        boutMap.put("fighter1", bout.getFighter1() != null ? new FighterDTO(bout.getFighter1()) : null);
        boutMap.put("fighter2", bout.getFighter2() != null ? new FighterDTO(bout.getFighter2()) : null);
        return this;
    }

    public BoutDTOBuilder withWinner() {
        boutMap.put("winner", bout.getWinner() != null ? new FighterDTO(bout.getWinner()) : null);
        return this;
    }

    public BoutDTOBuilder withDecision() {
        boutMap.put("decisionMethod", bout.getDecisionMethod() != null ? bout.getDecisionMethod().name() : null);
        boutMap.put("decisionType", bout.getDecisionType() != null ? bout.getDecisionType().name() : null);
        boutMap.put("finishRound", bout.getFinishRound());
        boutMap.put("finishTimeSeconds", bout.getFinishTimeSeconds());
        return this;
    }

    public BoutDTOBuilder withTimestamps() {
        boutMap.put("scheduledAt", bout.getScheduledAt() != null ? bout.getScheduledAt().toString() : null);
        boutMap.put("startedAt", bout.getStartedAt() != null ? bout.getStartedAt().toString() : null);
        boutMap.put("finishedAt", bout.getFinishedAt() != null ? bout.getFinishedAt().toString() : null);
        return this;
    }

    public BoutDTOBuilder withJudges() {
        List<User> judges = bout.getJudges();
        if (judges != null) {
            boutMap.put("judges", judges.stream()
                    .map(j -> Map.of("id", j.getId(), "fullName", j.getFullName()))
                    .collect(Collectors.toList()));
        }
        return this;
    }

    public BoutDTOBuilder withEvent() {
        if (bout.getEvent() != null) {
            boutMap.put("eventId", bout.getEvent().getId());
            boutMap.put("eventName", bout.getEvent().getName());
        }
        return this;
    }

    public BoutDTOBuilder withCustomField(String key, Object value) {
        boutMap.put(key, value);
        return this;
    }

    public BoutDTOBuilder buildBasicView() {
        return this.withBasicInfo().withFighters();
    }

    public BoutDTOBuilder buildStandardView() {
        return this.withBasicInfo().withFighters().withWinner().withDecision();
    }

    public BoutDTOBuilder buildDetailView() {
        return this.withBasicInfo()
                   .withFighters()
                   .withWinner()
                   .withDecision()
                   .withTimestamps()
                   .withJudges()
                   .withEvent();
    }

    public Map<String, Object> build() {
        return new HashMap<>(boutMap);
    }
}
