package com.example.mma.builder;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;

/**
 * =============================================================================
 * PATRÓN DE DISEÑO: BUILDER PATTERN - Para FighterDTO
 * =============================================================================
 * 
 * Builder para construir objetos FighterDTO de forma fluida.
 * 
 * Ventajas sobre constructor con muchos parámetros:
 * - Código más legible
 * - Parámetros opcionales sin sobrecarga de constructores
 * - Validación durante la construcción
 * 
 * Uso:
 * FighterDTO dto = FighterDTOBuilder.create()
 *     .withName("Jon", "Jones")
 *     .withCategory("LIGHT_HEAVYWEIGHT")
 *     .withRecord(27, 1, 0)
 *     .build();
 * 
 * @author MMA Live System
 * @version 2.0 - Implementación del patrón Builder
 */
public class FighterDTOBuilder {

    private Long id;
    private String firstName;
    private String lastName;
    private String club;
    private String categoryWeight;
    private String status = "Active"; // Valor por defecto
    private Integer recordW = 0;
    private Integer recordL = 0;
    private Integer recordD = 0;

    /**
     * Constructor privado - usar create() para iniciar
     */
    private FighterDTOBuilder() {}

    /**
     * Método factory para iniciar la construcción
     * 
     * @return Nuevo builder
     */
    public static FighterDTOBuilder create() {
        return new FighterDTOBuilder();
    }

    /**
     * Crea un builder a partir de una entidad existente
     * 
     * @param fighter Entidad Fighter fuente
     * @return Builder con datos de la entidad
     */
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

    /**
     * Establece el ID del peleador
     */
    public FighterDTOBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Establece nombre y apellido
     */
    public FighterDTOBuilder withName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }

    /**
     * Establece solo el nombre
     */
    public FighterDTOBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
     * Establece solo el apellido
     */
    public FighterDTOBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * Establece el club/gimnasio
     */
    public FighterDTOBuilder withClub(String club) {
        this.club = club;
        return this;
    }

    /**
     * Establece la categoría de peso (como String)
     */
    public FighterDTOBuilder withCategory(String categoryWeight) {
        this.categoryWeight = categoryWeight;
        return this;
    }

    /**
     * Establece la categoría de peso (como enum)
     */
    public FighterDTOBuilder withCategory(WeightCategory category) {
        this.categoryWeight = category != null ? category.name() : null;
        return this;
    }

    /**
     * Establece el estado del peleador (como String)
     */
    public FighterDTOBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * Establece el estado del peleador (como enum)
     */
    public FighterDTOBuilder withStatus(FighterStatus status) {
        this.status = status != null ? status.name() : "Active";
        return this;
    }

    /**
     * Establece el récord completo (W-L-D)
     */
    public FighterDTOBuilder withRecord(Integer wins, Integer losses, Integer draws) {
        this.recordW = wins != null ? wins : 0;
        this.recordL = losses != null ? losses : 0;
        this.recordD = draws != null ? draws : 0;
        return this;
    }

    /**
     * Establece las victorias
     */
    public FighterDTOBuilder withWins(Integer wins) {
        this.recordW = wins != null ? wins : 0;
        return this;
    }

    /**
     * Establece las derrotas
     */
    public FighterDTOBuilder withLosses(Integer losses) {
        this.recordL = losses != null ? losses : 0;
        return this;
    }

    /**
     * Establece los empates
     */
    public FighterDTOBuilder withDraws(Integer draws) {
        this.recordD = draws != null ? draws : 0;
        return this;
    }

    /**
     * Construye el FighterDTO final
     * 
     * @return FighterDTO construido
     * @throws IllegalStateException si faltan campos requeridos
     */
    public FighterDTO build() {
        // Validación de campos requeridos
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

    /**
     * Construye y convierte a entidad Fighter
     * 
     * @return Entidad Fighter construida
     */
    public Fighter buildEntity() {
        return build().toEntity();
    }
}

