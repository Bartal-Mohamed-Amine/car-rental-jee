package ma.uir.awd.dao;

import ma.uir.awd.model.StatutVehicule;
import ma.uir.awd.model.Vehicule;
import java.util.List;

/**
 * DAO interface for Vehicule. All queries parameterised (T6).
 */
public interface IVehiculeDao extends IBaseDao<Vehicule, Long> {

    /** Find by licence plate (unique). Returns null if not found. */
    Vehicule findByImmatriculation(String immatriculation);

    /** All vehicles in a given status. */
    List<Vehicule> findByStatut(StatutVehicule statut);

    /** All vehicles in a given category. */
    List<Vehicule> findByCategorie(Long categorieId);

    /**
     * Find vehicle by id with a PESSIMISTIC_WRITE lock.
     * Used by ReservationMetierImpl to prevent double-booking (R11, AD-04).
     */
    Vehicule findByIdForUpdate(Long id);

    /** All vehicles with their category eagerly fetched (avoids N+1 on list page). */
    List<Vehicule> findAllWithCategorie();
}
