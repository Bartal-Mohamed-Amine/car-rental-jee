package ma.uir.awd.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Hibernate session factory holder (singleton pattern).
 *
 * This class lives in the dao package because only the DAO layer (and the composition
 * root AppInitListener) should know about Hibernate. Metier classes never import this.
 *
 * Configuration is read from:
 *   1. src/main/resources/hibernate.cfg.xml  (entity list, dialect, pool settings)
 *   2. src/main/resources/app.properties     (DB URL/user/password)
 *   3. Environment variables (override for Docker: APP_DB_URL, APP_DB_USER, APP_DB_PASSWORD)
 *
 * This follows AD-08 (decisions.md): no credentials in source code.
 */
public final class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {}

    /**
     * Initialises the SessionFactory. Called once at startup by AppInitListener.
     * Thread-safe via double-checked locking.
     */
    public static synchronized void init() {
        if (sessionFactory != null) return;
        try {
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");

            // Override DB credentials from app.properties or environment variables.
            // Environment variables (set in docker-compose.yml) take priority.
            Properties appProps = loadAppProperties();
            String url  = envOrProp("APP_DB_URL",      appProps, "db.url");
            String user = envOrProp("APP_DB_USER",      appProps, "db.user");
            String pass = envOrProp("APP_DB_PASSWORD",  appProps, "db.password");

            if (url  != null) cfg.setProperty("hibernate.connection.url",      url);
            if (user != null) cfg.setProperty("hibernate.connection.username",  user);
            if (pass != null) cfg.setProperty("hibernate.connection.password",  pass);

            ServiceRegistry sr = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties())
                .build();
            sessionFactory = cfg.buildSessionFactory(sr);
            log.info("Hibernate SessionFactory initialised.");
        } catch (Exception ex) {
            log.error("Failed to initialise Hibernate SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /** Returns the shared session factory. Call init() first. */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("HibernateUtil.init() was not called at startup.");
        }
        return sessionFactory;
    }

    /**
     * Returns the current session bound to the calling thread.
     * Requires hibernate.current_session_context_class=thread (set in hibernate.cfg.xml).
     */
    public static Session currentSession() {
        return getSessionFactory().getCurrentSession();
    }

    /** Closes the session factory at application shutdown. */
    public static synchronized void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            sessionFactory = null;
            log.info("Hibernate SessionFactory closed.");
        }
    }

    // -------------------------------------------------------------------------

    private static Properties loadAppProperties() {
        Properties p = new Properties();
        try (InputStream is = HibernateUtil.class
                .getClassLoader().getResourceAsStream("app.properties")) {
            if (is != null) p.load(is);
        } catch (Exception e) {
            log.warn("Could not load app.properties: {}", e.getMessage());
        }
        return p;
    }

    private static String envOrProp(String envKey, Properties props, String propKey) {
        String val = System.getenv(envKey);
        if (val != null && !val.isBlank()) return val;
        return props.getProperty(propKey);
    }
}
