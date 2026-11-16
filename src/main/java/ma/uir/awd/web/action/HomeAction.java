package ma.uir.awd.web.action;

import ma.uir.awd.model.Role;
import ma.uir.awd.model.Utilisateur;

/**
 * Routes authenticated users to their role-specific home page (R2, R3, R4).
 */
public class HomeAction extends BaseAction {

    @Override
    public String execute() {
        Utilisateur user = getCurrentUser();
        if (user == null) {
            return "login";
        }
        if (user.getRole() == Role.ADMIN) {
            return "admin";
        } else if (user.getRole() == Role.AGENT) {
            return "agent";
        } else {
            return "client";
        }
    }
}
