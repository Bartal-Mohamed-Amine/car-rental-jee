package ma.uir.awd.web.action;

import com.opensymphony.xwork2.ActionSupport;
import ma.uir.awd.model.Role;
import ma.uir.awd.model.Utilisateur;
import org.apache.struts2.action.SessionAware;

import java.util.Map;

/**
 * Base action providing typed access to the current session and authenticated user.
 * Implements Struts 6 SessionAware (withSession method).
 */
public abstract class BaseAction extends ActionSupport implements SessionAware {

    public static final String SESSION_USER = "user";

    protected Map<String, Object> session;

    @Override
    public void withSession(Map<String, Object> session) {
        this.session = session;
    }

    public Utilisateur getCurrentUser() {
        if (session == null) return null;
        return (Utilisateur) session.get(SESSION_USER);
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public boolean hasRole(Role role) {
        Utilisateur user = getCurrentUser();
        return user != null && user.getRole() == role;
    }
}
