package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.HibernateUtil;
import ma.uir.awd.dao.IBaseDao;
import org.hibernate.Session;

import java.util.List;

/**
 * Generic base DAO implementation using Hibernate.
 * Concrete DAOs extend this, passing the entity class to the constructor.
 *
 * Sessions come from HibernateUtil.currentSession() — the calling thread must
 * be inside a transaction (started by ITransactionRunner) before calling any method.
 */
public abstract class BaseDaoImpl<T, ID> implements IBaseDao<T, ID> {

    private final Class<T> entityClass;

    protected BaseDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Session session() {
        return HibernateUtil.currentSession();
    }

    @Override
    public void save(T entity) {
        session().save(entity);
    }

    @Override
    public void update(T entity) {
        session().update(entity);
    }

    @Override
    public void saveOrUpdate(T entity) {
        session().saveOrUpdate(entity);
    }

    @Override
    public void delete(T entity) {
        session().delete(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T findById(ID id) {
        return session().get(entityClass, (java.io.Serializable) id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery<T> cq = session().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return session().createQuery(cq).getResultList();
    }
}
