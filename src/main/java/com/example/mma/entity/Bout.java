package com.example.mma.entity;

import com.example.mma.enums.BoutState;
import com.example.mma.enums.DecisionMethod;
import com.example.mma.enums.DecisionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bouts")
public class Bout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer boutNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fighter1_id", nullable = false)
    private Fighter fighter1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fighter2_id", nullable = false)
    private Fighter fighter2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private Fighter winner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoutState state = BoutState.Programada;

    @Column(nullable = false)
    private Integer totalRounds = 3;

    private Integer currentRound = 0;

    @Enumerated(EnumType.STRING)
    private DecisionMethod decisionMethod;

    @Enumerated(EnumType.STRING)
    private DecisionType decisionType;

    private Integer finishRound;
    private Integer finishTimeSeconds;

    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    // Jueces asignados
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "bout_judges",
        joinColumns = @JoinColumn(name = "bout_id"),
        inverseJoinColumns = @JoinColumn(name = "judge_id")
    )
    private List<User> judges = new ArrayList<>();

    // Tarjetas de puntuación
    @OneToMany(mappedBy = "bout", cascade = CascadeType.ALL)
    private List<ScoreCard> scoreCards = new ArrayList<>();

    public Bout() {}

    public Bout(Fighter fighter1, Fighter fighter2, Integer totalRounds) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.totalRounds = totalRounds;
        this.state = BoutState.Programada;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getBoutNumber() { return boutNumber; }
    public void setBoutNumber(Integer boutNumber) { this.boutNumber = boutNumber; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Fighter getFighter1() { return fighter1; }
    public void setFighter1(Fighter fighter1) { this.fighter1 = fighter1; }

    public Fighter getFighter2() { return fighter2; }
    public void setFighter2(Fighter fighter2) { this.fighter2 = fighter2; }

    public Fighter getWinner() { return winner; }
    public void setWinner(Fighter winner) { this.winner = winner; }

    public BoutState getState() { return state; }
    public void setState(BoutState state) { this.state = state; }

    public Integer getTotalRounds() { return totalRounds; }
    public void setTotalRounds(Integer totalRounds) { this.totalRounds = totalRounds; }

    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }

    public DecisionMethod getDecisionMethod() { return decisionMethod; }
    public void setDecisionMethod(DecisionMethod decisionMethod) { this.decisionMethod = decisionMethod; }

    public DecisionType getDecisionType() { return decisionType; }
    public void setDecisionType(DecisionType decisionType) { this.decisionType = decisionType; }

    public Integer getFinishRound() { return finishRound; }
    public void setFinishRound(Integer finishRound) { this.finishRound = finishRound; }

    public Integer getFinishTimeSeconds() { return finishTimeSeconds; }
    public void setFinishTimeSeconds(Integer finishTimeSeconds) { this.finishTimeSeconds = finishTimeSeconds; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    public List<User> getJudges() { return judges; }
    public void setJudges(List<User> judges) { this.judges = judges; }

    public List<ScoreCard> getScoreCards() { return scoreCards; }
    public void setScoreCards(List<ScoreCard> scoreCards) { this.scoreCards = scoreCards; }
}

