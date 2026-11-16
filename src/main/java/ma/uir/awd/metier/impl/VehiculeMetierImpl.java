package ma.uir.awd.metier.impl;

import ma.uir.awd.dao.ITransactionRunner;
import ma.uir.awd.dao.IVehiculeDao;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IVehiculeMetier;
import ma.uir.awd.model.Vehicule;

import java.util.List;

/**
 * Implementation of IVehiculeMetier.
 * Depends strictly on interfaces (IVehiculeDao, ITransactionRunner).
 * Never imports Hibernate or DAO implementation classes (T1).
 */
public class VehiculeMetierImpl implements IVehiculeMetier {

    private final IVehiculeDao vehiculeDao;
    private final ITransactionRunner tx;

    public VehiculeMetierImpl(IVehiculeDao vehiculeDao, ITransactionRunner tx) {
        this.vehiculeDao = vehiculeDao;
        this.tx = tx;
    }

    @Override
    public List<Vehicule> listAll() {
        return tx.inTransaction(vehiculeDao::findAllWithCategorie);
    }

    @Override
    public Vehicule findById(Long id) {
        if (id == null) {
            throw new BusinessException("error.vehicle.notfound");
        }
        Vehicule v = tx.inTransaction(() -> vehiculeDao.findById(id));
        if (v == null) {
            throw new BusinessException("error.vehicle.notfound");
        }
        return v;
    }
}
