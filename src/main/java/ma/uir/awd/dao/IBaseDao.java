package ma.uir.awd.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic base DAO interface.
 * Concrete entity DAOs extend this and add entity-specific finders.
 *
 * T = entity type, ID = primary key type
 */
public interface IBaseDao<T, ID> {

    /** Persist a new entity. */
    void save(T entity);

    /** Update an already-persistent entity. */
    void update(T entity);

    /** Save or update. */
    void saveOrUpdate(T entity);

    /** Delete an entity. */
    void delete(T entity);

    /** Find by primary key. Returns null if not found. */
    T findById(ID id);

    /** Find all rows (use with caution on large tables). */
    List<T> findAll();
}
