package com.example.mma.entity;

import com.example.mma.enums.StageType;
import jakarta.persistence.*;

@Entity
@Table(name = "tournament_matches")
public class TournamentMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageType stage;

    private Integer matchNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fighter1_id")
    private Fighter fighter1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fighter2_id")
    private Fighter fighter2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private Fighter winner;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bout_id")
    private Bout bout;

    // Referencia al siguiente match donde avanza el ganador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_match_id")
    private TournamentMatch nextMatch;

    private boolean completed = false;

    public TournamentMatch() {}

    public TournamentMatch(Tournament tournament, StageType stage, Integer matchNumber) {
        this.tournament = tournament;
        this.stage = stage;
        this.matchNumber = matchNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public StageType getStage() { return stage; }
    public void setStage(StageType stage) { this.stage = stage; }

    public Integer getMatchNumber() { return matchNumber; }
    public void setMatchNumber(Integer matchNumber) { this.matchNumber = matchNumber; }

    public Fighter getFighter1() { return fighter1; }
    public void setFighter1(Fighter fighter1) { this.fighter1 = fighter1; }

    public Fighter getFighter2() { return fighter2; }
    public void setFighter2(Fighter fighter2) { this.fighter2 = fighter2; }

    public Fighter getWinner() { return winner; }
    public void setWinner(Fighter winner) { this.winner = winner; }

    public Bout getBout() { return bout; }
    public void setBout(Bout bout) { this.bout = bout; }

    public TournamentMatch getNextMatch() { return nextMatch; }
    public void setNextMatch(TournamentMatch nextMatch) { this.nextMatch = nextMatch; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

