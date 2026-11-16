package ma.uir.awd.web.action;

import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IAuthMetier;
import ma.uir.awd.model.Utilisateur;
import ma.uir.awd.web.util.AppContext;
import org.apache.struts2.action.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Handles user login with server-side validation and session fixation prevention (R1, T9).
 */
public class LoginAction extends BaseAction implements ServletRequestAware {

    private String email;
    private String plainPassword;
    private HttpServletRequest httpRequest;

    @Override
    public void withServletRequest(HttpServletRequest request) {
        this.httpRequest = request;
    }

    @Override
    public String execute() {
        IAuthMetier authMetier = AppContext.get(IAuthMetier.class);
        try {
            Utilisateur user = authMetier.login(email, plainPassword);

            // Session fixation protection: invalidate old session and create a fresh one (AD-10)
            if (httpRequest != null) {
                HttpSession oldSession = httpRequest.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession newSession = httpRequest.getSession(true);
                newSession.setAttribute(SESSION_USER, user);
            } else if (session != null) {
                session.put(SESSION_USER, user);
            }

            return SUCCESS;
        } catch (BusinessException e) {
            addActionError(getText("error.login.invalid", "Email ou mot de passe incorrect."));
            return INPUT;
        }
    }

    @Override
    public void validate() {
        if (email == null || email.trim().isEmpty()) {
            addFieldError("email", getText("error.email.required", "L'adresse email est requise."));
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            addFieldError("plainPassword", getText("error.password.required", "Le mot de passe est requis."));
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPlainPassword() { return plainPassword; }
    public void setPlainPassword(String plainPassword) { this.plainPassword = plainPassword; }
}
