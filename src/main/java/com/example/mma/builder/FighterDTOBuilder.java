package com.example.mma.builder;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;

/**
 * Builder para construir DTOs de peleadores.
 * 
 * Patrón Builder:
 * Construcción fluida de objetos FighterDTO con validación.
 */
public class FighterDTOBuilder {

    private Long id;
    private String firstName;
    private String lastName;
    private String club;
    private String categoryWeight;
    private String status = "Active";
    private Integer recordW = 0;
    private Integer recordL = 0;
    private Integer recordD = 0;

    private FighterDTOBuilder() {}

    public static FighterDTOBuilder create() {
        return new FighterDTOBuilder();
    }

    public static FighterDTOBuilder from(Fighter fighter) {
        FighterDTOBuilder builder = new FighterDTOBuilder();
        builder.id = fighter.getId();
        builder.firstName = fighter.getFirstName();
        builder.lastName = fighter.getLastName();
        builder.club = fighter.getClub();
        builder.categoryWeight = fighter.getCategoryWeight() != null ? 
                                  fighter.getCategoryWeight().name() : null;
        builder.status = fighter.getStatus() != null ? 
                         fighter.getStatus().name() : "Active";
        builder.recordW = fighter.getRecordW();
        builder.recordL = fighter.getRecordL();
        builder.recordD = fighter.getRecordD();
        return builder;
    }

    public FighterDTOBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public FighterDTOBuilder withName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }

    public FighterDTOBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public FighterDTOBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public FighterDTOBuilder withClub(String club) {
        this.club = club;
        return this;
    }

    public FighterDTOBuilder withCategory(String categoryWeight) {
        this.categoryWeight = categoryWeight;
        return this;
    }

    public FighterDTOBuilder withCategory(WeightCategory category) {
        this.categoryWeight = category != null ? category.name() : null;
        return this;
    }

    public FighterDTOBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public FighterDTOBuilder withStatus(FighterStatus status) {
        this.status = status != null ? status.name() : "Active";
        return this;
    }

    public FighterDTOBuilder withRecord(Integer wins, Integer losses, Integer draws) {
        this.recordW = wins != null ? wins : 0;
        this.recordL = losses != null ? losses : 0;
        this.recordD = draws != null ? draws : 0;
        return this;
    }

    public FighterDTOBuilder withWins(Integer wins) {
        this.recordW = wins != null ? wins : 0;
        return this;
    }

    public FighterDTOBuilder withLosses(Integer losses) {
        this.recordL = losses != null ? losses : 0;
        return this;
    }

    public FighterDTOBuilder withDraws(Integer draws) {
        this.recordD = draws != null ? draws : 0;
        return this;
    }

    public FighterDTO build() {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalStateException("El nombre es requerido");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalStateException("El apellido es requerido");
        }

        FighterDTO dto = new FighterDTO();
        dto.setId(this.id);
        dto.setFirstName(this.firstName);
        dto.setLastName(this.lastName);
        dto.setClub(this.club);
        dto.setCategoryWeight(this.categoryWeight);
        dto.setStatus(this.status);
        dto.setRecordW(this.recordW);
        dto.setRecordL(this.recordL);
        dto.setRecordD(this.recordD);
        return dto;
    }

    public Fighter buildEntity() {
        return build().toEntity();
    }
}
