package ma.uir.awd.dao;

import ma.uir.awd.model.Parametres;

/**
 * DAO interface for the global Parametres singleton (id=1 always).
 */
public interface IParametresDao {

    /** Returns the global parameters (singleton row). Creates default if missing. */
    Parametres get();

    /** Persist updated parameters. */
    void save(Parametres p);
}
