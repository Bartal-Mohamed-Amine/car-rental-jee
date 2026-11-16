package ma.uir.awd.web.listener;

import ma.uir.awd.dao.*;
import ma.uir.awd.dao.impl.*;
import ma.uir.awd.metier.*;
import ma.uir.awd.metier.impl.*;
import ma.uir.awd.metier.ml.RiskMetierImpl;
import ma.uir.awd.web.util.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStream;
import java.util.Properties;

/**
 * Composition Root of the application.
 *
 * This is the ONLY class in the web layer permitted to reference DAO and Metier
 * implementation (*Impl) classes (T1 layering rule).
 *
 * It instantiates all singletons, wires dependencies, and registers them in AppContext.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppInitListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Car Rental Application initializing...");

        // 1. Initialize Hibernate SessionFactory
        HibernateUtil.init();

        // 2. Load model directory configuration
        Properties appProps = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (is != null) appProps.load(is);
        } catch (Exception e) {
            log.warn("Could not read app.properties: {}", e.getMessage());
        }
        String modelDir = System.getenv("APP_MODEL_DIR");
        if (modelDir == null || modelDir.isBlank()) {
            modelDir = appProps.getProperty("app.model.dir", "models");
        }

        // 3. Assemble DAO layer
        ITransactionRunner tx = new TransactionRunnerImpl();
        IUtilisateurDao utilisateurDao = new UtilisateurDaoImpl();
        IVehiculeDao vehiculeDao = new VehiculeDaoImpl();
        ICategorieDao categorieDao = new CategorieDaoImpl();
        IParametresDao parametresDao = new ParametresDaoImpl();

        // 4. Assemble Business layer
        IAuthMetier authMetier = new AuthMetierImpl(utilisateurDao, tx);
        IVehiculeMetier vehiculeMetier = new VehiculeMetierImpl(vehiculeDao, tx);
        IRiskMetier riskMetier = new RiskMetierImpl(modelDir);

        // 5. Register in AppContext
        AppContext.register(IAuthMetier.class, authMetier);
        AppContext.register(IVehiculeMetier.class, vehiculeMetier);
        AppContext.register(IRiskMetier.class, riskMetier);

        log.info("Car Rental Application successfully initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Car Rental Application shutting down...");
        AppContext.clear();
        HibernateUtil.shutdown();
        log.info("Car Rental Application shutdown complete.");
    }
}
