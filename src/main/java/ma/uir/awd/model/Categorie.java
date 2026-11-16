package ma.uir.awd.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle category (Economy, SUV, Luxury, Van …).
 * Admin manages categories (R2). Vehicles belong to exactly one category.
 */
@Entity
@Table(name = "categorie")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nom;

    @Column(length = 255)
    private String description;

    /** Lazy: use join fetch in DAO when the list is needed */
    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    private List<Vehicule> vehicules = new ArrayList<>();

    // -------------------------------------------------------------------------

    public Categorie() {}
    public Categorie(String nom, String description) {
        this.nom = nom; this.description = description;
    }

    public Long         getId()          { return id; }
    public String       getNom()         { return nom; }
    public String       getDescription() { return description; }
    public List<Vehicule> getVehicules() { return vehicules; }

    public void setId(Long id)                       { this.id = id; }
    public void setNom(String nom)                   { this.nom = nom; }
    public void setDescription(String description)   { this.description = description; }
    public void setVehicules(List<Vehicule> v)       { this.vehicules = v; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categorie)) return false;
        return Objects.equals(id, ((Categorie) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
    @Override public String toString() { return "Categorie{id=" + id + ", nom='" + nom + "'}"; }
}
