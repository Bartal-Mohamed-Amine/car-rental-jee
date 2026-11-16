package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.IUtilisateurDao;
import ma.uir.awd.model.Role;
import ma.uir.awd.model.Utilisateur;

import java.util.List;

/**
 * Parameterised HQL queries only — no string concatenation (T6).
 */
public class UtilisateurDaoImpl extends BaseDaoImpl<Utilisateur, Long>
        implements IUtilisateurDao {

    public UtilisateurDaoImpl() {
        super(Utilisateur.class);
    }

    @Override
    public Utilisateur findByEmail(String email) {
        return session()
            .createQuery("from Utilisateur u where u.email = :email", Utilisateur.class)
            .setParameter("email", email)
            .uniqueResult();
    }

    @Override
    public List<Utilisateur> findByRole(Role role) {
        return session()
            .createQuery("from Utilisateur u where u.role = :role order by u.nom", Utilisateur.class)
            .setParameter("role", role)
            .getResultList();
    }

    @Override
    public boolean emailExists(String email) {
        Long count = session()
            .createQuery("select count(u) from Utilisateur u where u.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        return count > 0;
    }
}
