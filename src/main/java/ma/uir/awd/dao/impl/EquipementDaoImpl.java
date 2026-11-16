package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.IEquipementDao;
import ma.uir.awd.model.Equipement;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Implementation of IEquipementDao. Parameterised queries only (T6).
 */
public class EquipementDaoImpl extends BaseDaoImpl<Equipement, Long> implements IEquipementDao {

    public EquipementDaoImpl() {
        super(Equipement.class);
    }

    @Override
    public List<Equipement> findActive() {
        return session()
            .createQuery("from Equipement e where e.actif = true order by e.nom", Equipement.class)
            .getResultList();
    }

    @Override
    public List<Equipement> findByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return session()
            .createQuery("from Equipement e where e.id in (:ids)", Equipement.class)
            .setParameterList("ids", ids)
            .getResultList();
    }
}
