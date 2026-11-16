package ma.uir.awd.web.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import ma.uir.awd.model.Utilisateur;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Ensures the authenticated user possesses one of the allowed roles for an action.
 * Configured via param:
 *   <param name="allowedRoles">ADMIN,AGENT</param>
 */
public class RoleInterceptor extends AbstractInterceptor {

    public static final String UNAUTHORIZED = "unauthorized";
    public static final String SESSION_USER = "user";

    private Set<String> allowedRoles = new HashSet<>();

    public void setAllowedRoles(String roles) {
        if (roles != null && !roles.isBlank()) {
            this.allowedRoles = new HashSet<>();
            for (String r : roles.split(",")) {
                this.allowedRoles.add(r.trim().toUpperCase());
            }
        }
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        Utilisateur user = (Utilisateur) session.get(SESSION_USER);

        if (user == null || user.getRole() == null) {
            return AuthInterceptor.LOGIN;
        }

        if (!allowedRoles.isEmpty() && !allowedRoles.contains(user.getRole().name())) {
            return UNAUTHORIZED;
        }

        return invocation.invoke();
    }
}
