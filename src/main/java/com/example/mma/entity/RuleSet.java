package com.example.mma.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rule_sets")
public class RuleSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer roundDurationSeconds = 300; // 5 minutos por defecto

    @Column(nullable = false)
    private Integer restDurationSeconds = 60; // 1 minuto de descanso

    @Column(nullable = false)
    private Integer defaultRounds = 3;

    @Column(nullable = false)
    private Integer maxRounds = 5;

    private boolean allowDraws = false;

    private boolean tenPointMustSystem = true;

    // Tipos de finalización permitidos
    private boolean allowKO = true;
    private boolean allowTKO = true;
    private boolean allowSubmission = true;
    private boolean allowDQ = true;

    public RuleSet() {}

    public RuleSet(String name, Integer roundDurationSeconds, Integer restDurationSeconds, Integer defaultRounds) {
        this.name = name;
        this.roundDurationSeconds = roundDurationSeconds;
        this.restDurationSeconds = restDurationSeconds;
        this.defaultRounds = defaultRounds;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRoundDurationSeconds() { return roundDurationSeconds; }
    public void setRoundDurationSeconds(Integer roundDurationSeconds) { this.roundDurationSeconds = roundDurationSeconds; }

    public Integer getRestDurationSeconds() { return restDurationSeconds; }
    public void setRestDurationSeconds(Integer restDurationSeconds) { this.restDurationSeconds = restDurationSeconds; }

    public Integer getDefaultRounds() { return defaultRounds; }
    public void setDefaultRounds(Integer defaultRounds) { this.defaultRounds = defaultRounds; }

    public Integer getMaxRounds() { return maxRounds; }
    public void setMaxRounds(Integer maxRounds) { this.maxRounds = maxRounds; }

    public boolean isAllowDraws() { return allowDraws; }
    public void setAllowDraws(boolean allowDraws) { this.allowDraws = allowDraws; }

    public boolean isTenPointMustSystem() { return tenPointMustSystem; }
    public void setTenPointMustSystem(boolean tenPointMustSystem) { this.tenPointMustSystem = tenPointMustSystem; }

    public boolean isAllowKO() { return allowKO; }
    public void setAllowKO(boolean allowKO) { this.allowKO = allowKO; }

    public boolean isAllowTKO() { return allowTKO; }
    public void setAllowTKO(boolean allowTKO) { this.allowTKO = allowTKO; }

    public boolean isAllowSubmission() { return allowSubmission; }
    public void setAllowSubmission(boolean allowSubmission) { this.allowSubmission = allowSubmission; }

    public boolean isAllowDQ() { return allowDQ; }
    public void setAllowDQ(boolean allowDQ) { this.allowDQ = allowDQ; }
}

