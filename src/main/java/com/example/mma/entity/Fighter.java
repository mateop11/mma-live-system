package com.example.mma.entity;

import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "fighters")
public class Fighter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeightCategory categoryWeight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FighterStatus status = FighterStatus.Active;

    private Integer recordW = 0;  // Wins
    private Integer recordL = 0;  // Losses
    private Integer recordD = 0;  // Draws

    public Fighter() {}

    public Fighter(String firstName, String lastName, WeightCategory categoryWeight) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.categoryWeight = categoryWeight;
        this.status = FighterStatus.Active;
        this.recordW = 0;
        this.recordL = 0;
        this.recordD = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public WeightCategory getCategoryWeight() {
        return categoryWeight;
    }

    public void setCategoryWeight(WeightCategory categoryWeight) {
        this.categoryWeight = categoryWeight;
    }

    public FighterStatus getStatus() {
        return status;
    }

    public void setStatus(FighterStatus status) {
        this.status = status;
    }

    public Integer getRecordW() {
        return recordW;
    }

    public void setRecordW(Integer recordW) {
        this.recordW = recordW;
    }

    public Integer getRecordL() {
        return recordL;
    }

    public void setRecordL(Integer recordL) {
        this.recordL = recordL;
    }

    public Integer getRecordD() {
        return recordD;
    }

    public void setRecordD(Integer recordD) {
        this.recordD = recordD;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addWin() {
        this.recordW++;
    }

    public void addLoss() {
        this.recordL++;
    }

    public void addDraw() {
        this.recordD++;
    }
}

