package ma.uir.awd.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Application user. Roles determine which screens are shown (R1-R4).
 * Password is ALWAYS stored as a BCrypt hash — never in clear text (R1).
 * Email is unique across all roles.
 */
@Entity
@Table(name = "utilisateur",
    indexes = {
        @Index(name = "idx_utilisateur_role", columnList = "role")
    }
)
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String prenom;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** BCrypt hash of the password — never exposed to the view layer */
    @Column(name = "password_hash", nullable = false, length = 72)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Column(nullable = false)
    private boolean actif = true;

    // -------------------------------------------------------------------------
    // Constructors

    public Utilisateur() {}

    public Utilisateur(String prenom, String nom, String email, String passwordHash, Role role) {
        this.prenom       = prenom;
        this.nom          = nom;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters

    public Long    getId()           { return id; }
    public String  getPrenom()       { return prenom; }
    public String  getNom()          { return nom; }
    public String  getEmail()        { return email; }
    public String  getPasswordHash() { return passwordHash; }
    public Role    getRole()         { return role; }
    public boolean isActif()         { return actif; }

    public void setId(Long id)                   { this.id = id; }
    public void setPrenom(String prenom)         { this.prenom = prenom; }
    public void setNom(String nom)               { this.nom = nom; }
    public void setEmail(String email)           { this.email = email; }
    public void setPasswordHash(String h)        { this.passwordHash = h; }
    public void setRole(Role role)               { this.role = role; }
    public void setActif(boolean actif)          { this.actif = actif; }

    // Convenience: full name used in views
    public String getNomComplet() { return prenom + " " + nom; }

    // -------------------------------------------------------------------------
    // equals / hashCode on identity (database id)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode() { return Objects.hashCode(id); }

    @Override public String toString() {
        return "Utilisateur{id=" + id + ", email='" + email + "', role=" + role + "}";
    }
}
