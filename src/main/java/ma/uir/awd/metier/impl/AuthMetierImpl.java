package ma.uir.awd.metier.impl;

import ma.uir.awd.dao.ITransactionRunner;
import ma.uir.awd.dao.IUtilisateurDao;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IAuthMetier;
import ma.uir.awd.model.Role;
import ma.uir.awd.model.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication business logic (R1, R4).
 *
 * Note on BCrypt: we use work factor 12 (see AD-09 decisions.md).
 * The password hash is verified here and ONLY here — no raw password
 * ever leaves this class (enforced by review and audit R1 check).
 *
 * This class is in metier.impl and may ONLY be named in AppInitListener (composition root).
 * All other callers use IAuthMetier.
 */
public class AuthMetierImpl implements IAuthMetier {

    private static final Logger log = LoggerFactory.getLogger(AuthMetierImpl.class);
    // BCrypt strength 12: ~100 ms per hash on modern hardware, acceptable for login
    private static final int BCRYPT_STRENGTH = 12;

    private final IUtilisateurDao utilisateurDao;
    private final ITransactionRunner tx;

    public AuthMetierImpl(IUtilisateurDao utilisateurDao, ITransactionRunner tx) {
        this.utilisateurDao = utilisateurDao;
        this.tx             = tx;
    }

    @Override
    public Utilisateur register(String prenom, String nom, String email, String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new BusinessException("error.password.empty");
        }
        if (prenom == null || prenom.isBlank() || nom == null || nom.isBlank()) {
            throw new BusinessException("error.name.empty");
        }
        if (email == null || !email.contains("@")) {
            throw new BusinessException("error.email.invalid");
        }

        return tx.inTransaction(() -> {
            // Check uniqueness inside the transaction
            if (utilisateurDao.emailExists(email)) {
                throw new BusinessException("error.email.exists");
            }
            // Hash BEFORE storing — plainPassword never touches the DB
            String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_STRENGTH));
            Utilisateur u = new Utilisateur(prenom.trim(), nom.trim(), email.trim().toLowerCase(), hash, Role.CLIENT);
            utilisateurDao.save(u);
            log.info("New customer registered: {}", u.getEmail());
            return u;
        });
    }

    @Override
    public Utilisateur login(String email, String plainPassword) {
        // Generic error message regardless of which field is wrong (prevents enumeration attacks)
        final String INVALID = "error.login.invalid";

        if (email == null || plainPassword == null) {
            throw new BusinessException(INVALID);
        }

        // This runs outside a transaction because it's a read with BCrypt check
        // which can take ~100ms — we don't want to hold a DB lock during that time.
        Utilisateur u = tx.inTransaction(() ->
            utilisateurDao.findByEmail(email.trim().toLowerCase())
        );

        if (u == null || !u.isActif()) {
            // BCrypt delay even on a miss to prevent timing-based email enumeration
            BCrypt.hashpw("dummy", BCrypt.gensalt(4));
            throw new BusinessException(INVALID);
        }

        if (!BCrypt.checkpw(plainPassword, u.getPasswordHash())) {
            throw new BusinessException(INVALID);
        }

        log.info("User logged in: {} role={}", u.getEmail(), u.getRole());
        return u;
    }

    @Override
    public Role getRole(Utilisateur utilisateur) {
        return utilisateur.getRole();
    }
}
