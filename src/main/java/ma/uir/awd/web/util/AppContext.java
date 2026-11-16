package ma.uir.awd.web.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service registry (Service Locator / Context pattern).
 * Populated ONLY by the composition root AppInitListener at application startup.
 * Actions look up business services by interface type:
 *     IAuthMetier auth = AppContext.get(IAuthMetier.class);
 *
 * This keeps actions strictly decoupled from implementation classes (T1).
 */
public final class AppContext {

    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    private AppContext() {}

    /** Register a service instance by its interface class. */
    public static <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    /** Retrieve a service instance by interface class. */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        Object service = services.get(type);
        if (service == null) {
            throw new IllegalStateException("Service not registered in AppContext: " + type.getName());
        }
        return (T) service;
    }

    /** Clear registry (used during shutdown). */
    public static void clear() {
        services.clear();
    }
}
