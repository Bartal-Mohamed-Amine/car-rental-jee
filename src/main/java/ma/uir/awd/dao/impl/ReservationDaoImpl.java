package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.IReservationDao;
import ma.uir.awd.model.Reservation;
import ma.uir.awd.model.StatutReservation;

import java.util.List;

/**
 * Implementation of IReservationDao. Parameterised HQL queries only (T6).
 */
public class ReservationDaoImpl extends BaseDaoImpl<Reservation, Long> implements IReservationDao {

    public ReservationDaoImpl() {
        super(Reservation.class);
    }

    @Override
    public List<Reservation> findByClient(Long clientId) {
        return session()
            .createQuery("from Reservation r join fetch r.vehicule v join fetch v.categorie where r.client.id = :cid order by r.dateDebut desc", Reservation.class)
            .setParameter("cid", clientId)
            .getResultList();
    }

    @Override
    public List<Reservation> findActiveByVehicule(Long vehiculeId) {
        return session()
            .createQuery("from Reservation r where r.vehicule.id = :vid and r.statut in (:actifs) order by r.dateDebut", Reservation.class)
            .setParameter("vid", vehiculeId)
            .setParameterList("actifs", List.of(StatutReservation.CONFIRMED, StatutReservation.PICKED_UP))
            .getResultList();
    }

    @Override
    public List<Reservation> findCompletedForTraining() {
        return session()
            .createQuery("from Reservation r join fetch r.vehicule v join fetch r.client where r.statut in (:terminal) order by r.dateDebut", Reservation.class)
            .setParameterList("terminal", List.of(StatutReservation.RETURNED, StatutReservation.CANCELLED))
            .getResultList();
    }
}
