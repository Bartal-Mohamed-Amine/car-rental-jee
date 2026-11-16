package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.IVehiculeDao;
import ma.uir.awd.model.StatutVehicule;
import ma.uir.awd.model.Vehicule;
import org.hibernate.LockMode;

import java.util.List;

/**
 * Parameterised HQL queries only (T6). join fetch used where needed to avoid N+1.
 */
public class VehiculeDaoImpl extends BaseDaoImpl<Vehicule, Long>
        implements IVehiculeDao {

    public VehiculeDaoImpl() {
        super(Vehicule.class);
    }

    @Override
    public Vehicule findByImmatriculation(String immatriculation) {
        return session()
            .createQuery("from Vehicule v where v.immatriculation = :immat", Vehicule.class)
            .setParameter("immat", immatriculation)
            .uniqueResult();
    }

    @Override
    public List<Vehicule> findByStatut(StatutVehicule statut) {
        return session()
            .createQuery("from Vehicule v join fetch v.categorie where v.statut = :statut order by v.marque", Vehicule.class)
            .setParameter("statut", statut)
            .getResultList();
    }

    @Override
    public List<Vehicule> findByCategorie(Long categorieId) {
        return session()
            .createQuery("from Vehicule v join fetch v.categorie c where c.id = :cid order by v.marque", Vehicule.class)
            .setParameter("cid", categorieId)
            .getResultList();
    }

    /**
     * Acquires a PESSIMISTIC_WRITE lock on the vehicle row.
     * Used by ReservationMetierImpl during booking to prevent double-booking (R11, AD-04).
     * Must be called inside a transaction.
     */
    @Override
    public Vehicule findByIdForUpdate(Long id) {
        return session().get(Vehicule.class, id, LockMode.PESSIMISTIC_WRITE);
    }

    /**
     * Load all vehicles with their category in one query.
     * Avoids N+1 selects when rendering the vehicle list page (AD-03).
     */
    @Override
    public List<Vehicule> findAllWithCategorie() {
        return session()
            .createQuery("from Vehicule v join fetch v.categorie order by v.marque, v.modele", Vehicule.class)
            .getResultList();
    }
}
