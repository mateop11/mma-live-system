package com.example.mma;

import jakarta.persistence.*;

@Entity
public class Fight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fighter1_id")
    private Fighter fighter1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fighter2_id")
    private Fighter fighter2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private Fighter winner;

    private Integer rounds;
    private String status;

    public Fight() {}

    public Fight(Fighter fighter1, Fighter fighter2, Fighter winner, Integer rounds, String status) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.winner = winner;
        this.rounds = rounds;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Fighter getFighter1() { return fighter1; }
    public void setFighter1(Fighter fighter1) { this.fighter1 = fighter1; }

    public Fighter getFighter2() { return fighter2; }
    public void setFighter2(Fighter fighter2) { this.fighter2 = fighter2; }

    public Fighter getWinner() { return winner; }
    public void setWinner(Fighter winner) { this.winner = winner; }

    public Integer getRounds() { return rounds; }
    public void setRounds(Integer rounds) { this.rounds = rounds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
