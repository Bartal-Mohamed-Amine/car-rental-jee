package ma.uir.awd.metier;

import ma.uir.awd.model.Role;
import ma.uir.awd.model.Utilisateur;

/**
 * Authentication and registration (R1, R4).
 *
 * This interface is all the web layer ever needs; it must never see BCrypt or DAO impls.
 */
public interface IAuthMetier {

    /**
     * Register a new customer account.
     * Hashes the password with BCrypt before saving.
     * Throws BusinessException("error.email.exists") if the email is taken.
     * Throws BusinessException("error.password.empty") if the password is blank.
     */
    Utilisateur register(String prenom, String nom, String email, String plainPassword);

    /**
     * Authenticate by email + plain-text password.
     * Returns the matching user, or throws BusinessException("error.login.invalid")
     * if the email/password combination is wrong (generic message — R1 security).
     */
    Utilisateur login(String email, String plainPassword);

    /**
     * Returns the role of the authenticated user. Convenience method used by the
     * auth interceptor to determine which screens are allowed.
     */
    Role getRole(Utilisateur utilisateur);
}
