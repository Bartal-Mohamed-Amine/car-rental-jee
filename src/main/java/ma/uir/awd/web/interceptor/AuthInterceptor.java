package ma.uir.awd.web.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import ma.uir.awd.model.Utilisateur;

import java.util.Map;

/**
 * Ensures a user is authenticated in the current session (R1).
 * If unauthenticated, redirects to the login result.
 */
public class AuthInterceptor extends AbstractInterceptor {

    public static final String LOGIN = "login";
    public static final String SESSION_USER = "user";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        Utilisateur user = (Utilisateur) session.get(SESSION_USER);

        if (user == null || !user.isActif()) {
            return LOGIN;
        }

        return invocation.invoke();
    }
}
