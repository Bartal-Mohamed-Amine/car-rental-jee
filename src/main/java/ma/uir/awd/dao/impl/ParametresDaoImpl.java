package ma.uir.awd.dao.impl;

import ma.uir.awd.dao.HibernateUtil;
import ma.uir.awd.dao.IParametresDao;
import ma.uir.awd.model.Parametres;

import java.math.BigDecimal;

public class ParametresDaoImpl implements IParametresDao {

    @Override
    public Parametres get() {
        Parametres p = HibernateUtil.currentSession().get(Parametres.class, 1L);
        if (p == null) {
            // Create defaults: 20% VAT, 1.5x late-fee multiplier
            p = new Parametres(new BigDecimal("0.2000"), new BigDecimal("1.5000"));
            HibernateUtil.currentSession().save(p);
        }
        return p;
    }

    @Override
    public void save(Parametres p) {
        HibernateUtil.currentSession().saveOrUpdate(p);
    }
}
