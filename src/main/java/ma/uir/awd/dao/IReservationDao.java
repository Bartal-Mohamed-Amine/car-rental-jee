package ma.uir.awd.dao;

import ma.uir.awd.model.Reservation;
import java.util.List;

/**
 * DAO interface for Reservation (R10, T2).
 */
public interface IReservationDao extends IBaseDao<Reservation, Long> {

    /** Find all reservations belonging to a client. */
    List<Reservation> findByClient(Long clientId);

    /**
     * Find active reservations (CONFIRMED or PICKED_UP) for a vehicle.
     * Used by the conflict detection engine in metier (R11).
     */
    List<Reservation> findActiveByVehicule(Long vehiculeId);

    /**
     * Find completed or cancelled reservations for ML model training (R16).
     */
    List<Reservation> findCompletedForTraining();
}
