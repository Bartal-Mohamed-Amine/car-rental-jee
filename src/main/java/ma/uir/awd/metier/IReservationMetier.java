package ma.uir.awd.metier;

import ma.uir.awd.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Business interface for Reservation management (R10, R11, T2, T4).
 */
public interface IReservationMetier {

    /**
     * Create a new reservation with conflict detection and pessimistic locking (R11, AD-04).
     *
     * @param clientId       ID of the authenticated customer
     * @param vehiculeId     ID of the requested vehicle
     * @param debut          Requested start date (inclusive)
     * @param fin            Requested end date (inclusive)
     * @param equipementIds  Set of optional equipment IDs chosen by customer
     * @return Newly created and persisted Reservation with snapshots
     * @throws BusinessException if dates are invalid, vehicle is unavailable or under maintenance
     */
    Reservation reserver(Long clientId, Long vehiculeId, LocalDate debut, LocalDate fin, Set<Long> equipementIds);

    /**
     * Cancel an existing CONFIRMED reservation.
     * Only the owning client or an agent/admin may cancel.
     */
    void annuler(Long actingUserId, Long reservationId);

    /**
     * Customer reservation history (R14).
     */
    List<Reservation> historiqueClient(Long clientId);
}
