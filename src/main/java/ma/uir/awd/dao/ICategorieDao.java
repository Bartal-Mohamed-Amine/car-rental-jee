package ma.uir.awd.dao;

import ma.uir.awd.model.Categorie;
import java.util.List;

/**
 * DAO interface for Categorie. Parameterised queries only (T6).
 */
public interface ICategorieDao extends IBaseDao<Categorie, Long> {

    /** Find by exact name (unique). Returns null if not found. */
    Categorie findByNom(String nom);

    /** All categories with their vehicle count (not loading vehicles themselves). */
    List<Categorie> findAll();
}
