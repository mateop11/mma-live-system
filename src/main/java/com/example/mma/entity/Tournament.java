package com.example.mma.entity;

import com.example.mma.enums.WeightCategory;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private WeightCategory weightCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<TournamentMatch> matches = new ArrayList<>();

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean completed = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "champion_id")
    private Fighter champion;

    public Tournament() {}

    public Tournament(String name, WeightCategory weightCategory) {
        this.name = name;
        this.weightCategory = weightCategory;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public WeightCategory getWeightCategory() { return weightCategory; }
    public void setWeightCategory(WeightCategory weightCategory) { this.weightCategory = weightCategory; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public List<TournamentMatch> getMatches() { return matches; }
    public void setMatches(List<TournamentMatch> matches) { this.matches = matches; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Fighter getChampion() { return champion; }
    public void setChampion(Fighter champion) { this.champion = champion; }
}

