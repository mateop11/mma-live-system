package com.example.mma.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "score_cards")
public class ScoreCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bout_id", nullable = false)
    private Bout bout;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;

    @Column(nullable = false)
    private Integer roundNumber;

    // Puntajes (sistema 10-point must)
    @Column(nullable = false)
    private Integer fighter1Score = 10;

    @Column(nullable = false)
    private Integer fighter2Score = 10;

    private String notes;

    public ScoreCard() {}

    public ScoreCard(Bout bout, User judge, Integer roundNumber) {
        this.bout = bout;
        this.judge = judge;
        this.roundNumber = roundNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bout getBout() { return bout; }
    public void setBout(Bout bout) { this.bout = bout; }

    public User getJudge() { return judge; }
    public void setJudge(User judge) { this.judge = judge; }

    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }

    public Integer getFighter1Score() { return fighter1Score; }
    public void setFighter1Score(Integer fighter1Score) { this.fighter1Score = fighter1Score; }

    public Integer getFighter2Score() { return fighter2Score; }
    public void setFighter2Score(Integer fighter2Score) { this.fighter2Score = fighter2Score; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

