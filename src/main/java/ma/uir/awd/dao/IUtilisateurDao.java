package ma.uir.awd.dao;

import ma.uir.awd.model.Utilisateur;
import ma.uir.awd.model.Role;
import java.util.List;

/**
 * DAO interface for Utilisateur. All queries are parameterised (T6).
 */
public interface IUtilisateurDao extends IBaseDao<Utilisateur, Long> {

    /** Find by email (unique). Returns null if not found. */
    Utilisateur findByEmail(String email);

    /** Find all users with a given role. */
    List<Utilisateur> findByRole(Role role);

    /** True if the email is already used by any user. */
    boolean emailExists(String email);
}
