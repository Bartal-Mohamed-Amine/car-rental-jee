package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.HibernateUtil;
import ma.uir.awd.dao.ITransactionRunner;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * ITransactionRunner implementation using Hibernate's thread-bound session context.
 *
 * WHY: Metier code passes a lambda; this class handles begin/commit/rollback uniformly.
 * The session is bound to the calling thread via current_session_context_class=thread
 * (set in hibernate.cfg.xml). DAOs call HibernateUtil.currentSession() without worrying
 * about transaction management.
 */
public class TransactionRunnerImpl implements ITransactionRunner {

    private static final Logger log = LoggerFactory.getLogger(TransactionRunnerImpl.class);

    @Override
    public <T> T inTransaction(Supplier<T> work) {
        Session session = HibernateUtil.currentSession();
        Transaction tx = session.beginTransaction();
        try {
            T result = work.get();
            tx.commit();
            return result;
        } catch (Exception ex) {
            if (tx.getStatus().canRollback()) {
                tx.rollback();
            }
            log.error("Transaction rolled back: {}", ex.getMessage(), ex);
            throw ex;  // rethrow — metier catches BusinessException, others propagate
        }
        // Note: with current_session_context_class=thread, the session is bound to
        // the thread and closed automatically when the transaction ends (commit or rollback).
    }
}
