package ma.uir.awd.web.action;

import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IAuthMetier;
import ma.uir.awd.web.util.AppContext;

/**
 * Handles customer self-registration with server-side validation (R4, T9).
 */
public class RegisterAction extends BaseAction {

    private String prenom;
    private String nom;
    private String email;
    private String plainPassword;
    private String confirmPassword;

    @Override
    public String execute() {
        IAuthMetier authMetier = AppContext.get(IAuthMetier.class);
        try {
            authMetier.register(prenom, nom, email, plainPassword);
            addActionMessage(getText("register.success", "Compte créé avec succès ! Veuillez vous connecter."));
            return SUCCESS;
        } catch (BusinessException e) {
            addActionError(getText(e.getKey(), "Impossible de créer le compte. Cet email est peut-être déjà utilisé."));
            return INPUT;
        }
    }

    @Override
    public void validate() {
        if (prenom == null || prenom.trim().isEmpty()) {
            addFieldError("prenom", getText("error.prenom.required", "Le prénom est requis."));
        }
        if (nom == null || nom.trim().isEmpty()) {
            addFieldError("nom", getText("error.nom.required", "Le nom est requis."));
        }
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            addFieldError("email", getText("error.email.invalid", "Adresse email invalide."));
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            addFieldError("plainPassword", getText("error.password.length", "Le mot de passe doit contenir au moins 6 caractères."));
        }
        if (confirmPassword == null || !confirmPassword.equals(plainPassword)) {
            addFieldError("confirmPassword", getText("error.password.mismatch", "Les mots de passe ne correspondent pas."));
        }
    }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPlainPassword() { return plainPassword; }
    public void setPlainPassword(String plainPassword) { this.plainPassword = plainPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
