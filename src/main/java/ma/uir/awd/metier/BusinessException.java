package ma.uir.awd.metier;

/**
 * Exception thrown when a business rule is violated.
 * Carries a message key (not a message string) so the web layer can
 * look up the localised error message via getText(key) in the action.
 *
 * WHY a key instead of a message: keeps business logic language-independent (AD-09).
 */
public class BusinessException extends RuntimeException {

    private final String key;

    public BusinessException(String key) {
        super(key);
        this.key = key;
    }

    public BusinessException(String key, String detail) {
        super(key + ": " + detail);
        this.key = key;
    }

    public BusinessException(String key, Throwable cause) {
        super(key, cause);
        this.key = key;
    }

    /** The i18n message key — pass to getText() in the action. */
    public String getKey() { return key; }
}
