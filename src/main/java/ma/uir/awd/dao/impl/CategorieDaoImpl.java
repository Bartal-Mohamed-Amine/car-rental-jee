package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.ICategorieDao;
import ma.uir.awd.model.Categorie;

import java.util.List;

public class CategorieDaoImpl extends BaseDaoImpl<Categorie, Long>
        implements ICategorieDao {

    public CategorieDaoImpl() {
        super(Categorie.class);
    }

    @Override
    public Categorie findByNom(String nom) {
        return session()
            .createQuery("from Categorie c where c.nom = :nom", Categorie.class)
            .setParameter("nom", nom)
            .uniqueResult();
    }

    @Override
    public List<Categorie> findAll() {
        return session()
            .createQuery("from Categorie c order by c.nom", Categorie.class)
            .getResultList();
    }
}
