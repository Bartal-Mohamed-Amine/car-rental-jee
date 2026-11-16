package ma.uir.awd.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Optional rental equipment (GPS, child seat, extra driver …) with its own daily price (R8).
 * A reservation may include multiple equipment items (many-to-many, R10, T5).
 */
@Entity
@Table(name = "equipement")
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(length = 255)
    private String description;

    /** Daily price for this equipment item */
    @Column(name = "prix_journalier", nullable = false, precision = 8, scale = 2)
    private BigDecimal prixJournalier;

    /** Available for new reservations */
    @Column(nullable = false)
    private boolean actif = true;

    @ManyToMany(mappedBy = "equipements", fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();

    // -------------------------------------------------------------------------

    public Equipement() {}
    public Equipement(String nom, BigDecimal prixJournalier) {
        this.nom = nom; this.prixJournalier = prixJournalier;
    }

    public Long       getId()              { return id; }
    public String     getNom()             { return nom; }
    public String     getDescription()     { return description; }
    public BigDecimal getPrixJournalier()  { return prixJournalier; }
    public boolean    isActif()            { return actif; }

    public void setId(Long id)                          { this.id = id; }
    public void setNom(String nom)                      { this.nom = nom; }
    public void setDescription(String description)      { this.description = description; }
    public void setPrixJournalier(BigDecimal p)         { this.prixJournalier = p; }
    public void setActif(boolean actif)                 { this.actif = actif; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipement)) return false;
        return Objects.equals(id, ((Equipement) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
