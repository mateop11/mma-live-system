package com.example.mma.service.interfaces;

import com.example.mma.dto.FighterDTO;
import com.example.mma.entity.Fighter;
import com.example.mma.enums.FighterStatus;
import com.example.mma.enums.WeightCategory;

import java.util.List;
import java.util.Optional;

/**
 * =============================================================================
 * PRINCIPIO SOLID: DEPENDENCY INVERSION PRINCIPLE (DIP)
 * =============================================================================
 * 
 * Esta interfaz define el contrato para el servicio de peleadores.
 * 
 * DIP establece que:
 * - Los módulos de alto nivel no deben depender de módulos de bajo nivel
 * - Ambos deben depender de abstracciones (interfaces)
 * 
 * Beneficios:
 * - Desacoplamiento: Los controllers dependen de la interfaz, no de la implementación
 * - Testabilidad: Facilita crear mocks para pruebas unitarias
 * - Flexibilidad: Permite cambiar la implementación sin modificar los consumidores
 * 
 * @author MMA Live System
 * @version 2.0 - Refactorizado con principios SOLID
 */
public interface IFighterService {

    // ==================== OPERACIONES DE LECTURA ====================
    
    /**
     * Obtiene todos los peleadores del sistema
     * @return Lista de DTOs de peleadores
     */
    List<FighterDTO> findAll();

    /**
     * Busca un peleador por su ID
     * @param id Identificador del peleador
     * @return Optional con el DTO del peleador si existe
     */
    Optional<FighterDTO> findById(Long id);

    /**
     * Busca la entidad del peleador por su ID (uso interno)
     * @param id Identificador del peleador
     * @return Optional con la entidad Fighter
     */
    Optional<Fighter> findEntityById(Long id);

    /**
     * Filtra peleadores por estado
     * @param status Estado del peleador (Active, Inactive, etc.)
     * @return Lista de peleadores con ese estado
     */
    List<FighterDTO> findByStatus(FighterStatus status);

    /**
     * Filtra peleadores por categoría de peso
     * @param category Categoría de peso
     * @return Lista de peleadores en esa categoría
     */
    List<FighterDTO> findByCategory(WeightCategory category);

    /**
     * Busca peleadores por nombre
     * @param query Término de búsqueda
     * @return Lista de peleadores que coinciden
     */
    List<FighterDTO> search(String query);

    // ==================== OPERACIONES DE ESCRITURA ====================
    
    /**
     * Crea un nuevo peleador
     * @param dto Datos del peleador a crear
     * @return DTO del peleador creado
     */
    FighterDTO create(FighterDTO dto);

    /**
     * Actualiza un peleador existente
     * @param id ID del peleador a actualizar
     * @param dto Nuevos datos del peleador
     * @return Optional con el DTO actualizado si existe
     */
    Optional<FighterDTO> update(Long id, FighterDTO dto);

    /**
     * Elimina un peleador
     * @param id ID del peleador a eliminar
     * @return true si se eliminó correctamente
     */
    boolean delete(Long id);

    // ==================== OPERACIONES DE RÉCORD ====================
    
    /**
     * Registra una victoria para el peleador
     * @param fighterId ID del peleador ganador
     */
    void addWin(Long fighterId);

    /**
     * Registra una derrota para el peleador
     * @param fighterId ID del peleador perdedor
     */
    void addLoss(Long fighterId);

    /**
     * Registra un empate para ambos peleadores
     * @param fighter1Id ID del primer peleador
     * @param fighter2Id ID del segundo peleador
     */
    void addDraw(Long fighter1Id, Long fighter2Id);
}

