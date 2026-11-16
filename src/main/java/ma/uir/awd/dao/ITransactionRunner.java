package ma.uir.awd.dao;

import java.util.function.Supplier;

/**
 * Runs a unit of work inside a single Hibernate transaction.
 *
 * WHY this exists (AD-02 decisions.md):
 * Business rules that need to be atomic (e.g. check conflict THEN save reservation)
 * must run in one transaction. This interface lives in the dao package so that metier
 * can depend on it without importing any Hibernate class (T1 layer rule).
 *
 * Usage in metier:
 *   return tx.inTransaction(() -> {
 *       Vehicule v = vehiculeDao.findByIdForUpdate(id);  // PESSIMISTIC_WRITE lock
 *       if (overlaps) throw new BusinessException("...");
 *       reservationDao.save(r);
 *       return r;
 *   });
 */
public interface ITransactionRunner {

    /** Runs `work` in a transaction; commits on success, rolls back on exception. */
    <T> T inTransaction(Supplier<T> work);

    /** Convenience overload for void operations. */
    default void inTransaction(Runnable work) {
        inTransaction(() -> { work.run(); return null; });
    }
}
