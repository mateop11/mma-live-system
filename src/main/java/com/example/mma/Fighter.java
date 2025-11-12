package com.example.mma;

import jakarta.persistence.*;

@Entity
public class Fighter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String club;
    private String categoryWeight;
    private Integer recordW;
    private Integer recordL;
    private Integer recordD;
    private String status;

    public Fighter() {
    }

    public Fighter(String firstName, String lastName, String club, String categoryWeight,
                   Integer recordW, Integer recordL, Integer recordD, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.club = club;
        this.categoryWeight = categoryWeight;
        this.recordW = recordW;
        this.recordL = recordL;
        this.recordD = recordD;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getCategoryWeight() { return categoryWeight; }
    public void setCategoryWeight(String categoryWeight) { this.categoryWeight = categoryWeight; }

    public Integer getRecordW() { return recordW; }
    public void setRecordW(Integer recordW) { this.recordW = recordW; }

    public Integer getRecordL() { return recordL; }
    public void setRecordL(Integer recordL) { this.recordL = recordL; }

    public Integer getRecordD() { return recordD; }
    public void setRecordD(Integer recordD) { this.recordD = recordD; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
