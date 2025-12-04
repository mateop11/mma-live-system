package com.example.mma.dto;

import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;

public class FighterDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String club;
    private String categoryWeight;
    private String status;
    private Integer recordW;
    private Integer recordL;
    private Integer recordD;

    public FighterDTO() {}

    public FighterDTO(Fighter fighter) {
        this.id = fighter.getId();
        this.firstName = fighter.getFirstName();
        this.lastName = fighter.getLastName();
        this.club = fighter.getClub();
        this.categoryWeight = fighter.getCategoryWeight() != null ? fighter.getCategoryWeight().name() : null;
        this.status = fighter.getStatus() != null ? fighter.getStatus().name() : null;
        this.recordW = fighter.getRecordW();
        this.recordL = fighter.getRecordL();
        this.recordD = fighter.getRecordD();
    }

    public Fighter toEntity() {
        Fighter fighter = new Fighter();
        fighter.setId(this.id);
        fighter.setFirstName(this.firstName);
        fighter.setLastName(this.lastName);
        fighter.setClub(this.club);
        if (this.categoryWeight != null) {
            fighter.setCategoryWeight(WeightCategory.valueOf(this.categoryWeight));
        }
        if (this.status != null) {
            fighter.setStatus(FighterStatus.valueOf(this.status));
        }
        fighter.setRecordW(this.recordW != null ? this.recordW : 0);
        fighter.setRecordL(this.recordL != null ? this.recordL : 0);
        fighter.setRecordD(this.recordD != null ? this.recordD : 0);
        return fighter;
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

    public String getCategoryWeight() {
        return categoryWeight;
    }

    public void setCategoryWeight(String categoryWeight) {
        this.categoryWeight = categoryWeight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}

