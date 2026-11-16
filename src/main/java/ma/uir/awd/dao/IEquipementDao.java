package ma.uir.awd.dao;

import ma.uir.awd.model.Equipement;
import java.util.List;
import java.util.Set;

/**
 * DAO interface for Equipement entity (R8, T2).
 */
public interface IEquipementDao extends IBaseDao<Equipement, Long> {

    /** Find all active equipment available for selection. */
    List<Equipement> findActive();

    /** Find a set of equipment items by their primary keys. */
    List<Equipement> findByIds(Set<Long> ids);
}
