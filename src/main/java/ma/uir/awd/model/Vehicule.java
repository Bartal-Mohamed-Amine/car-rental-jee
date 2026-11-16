package ma.uir.awd.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fleet vehicle (R5, R6).
 *
 * Key invariants (enforced at the metier layer):
 * - immatriculation is unique.
 * - status transitions: AVAILABLE→RENTED at pick-up, RENTED→AVAILABLE at return,
 *   MAINTENANCE set only when no PICKED_UP rental exists.
 * - photo is a generated filename stored outside the WAR (never the client filename).
 * - tarifJournalier uses BigDecimal (never double) with scale 2 (AD-05).
 */
@Entity
@Table(name = "vehicule",
    indexes = {
        @Index(name = "idx_vehicule_status",    columnList = "statut"),
        @Index(name = "idx_vehicule_categorie", columnList = "categorie_id")
    }
)
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String marque;

    @Column(nullable = false, length = 60)
    private String modele;

    /** Unique licence plate — never editable after creation */
    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Carburant carburant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private BoiteVitesse boite;

    @Column(nullable = false)
    private int places;

    /** Daily rental rate — BigDecimal, scale 2, RoundingMode.HALF_UP (AD-05) */
    @Column(name = "tarif_journalier", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifJournalier;

    @Column(nullable = false)
    private int kilometrage;

    /** Generated filename of the uploaded photo; null until uploaded. File lives outside WAR. */
    @Column(length = 255)
    private String photo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatutVehicule statut = StatutVehicule.AVAILABLE;

    // --- Associations ---

    /** Lazy: load with join fetch when you need them */
    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Maintenance> maintenances = new ArrayList<>();

    // -------------------------------------------------------------------------

    public Vehicule() {}

    // -------------------------------------------------------------------------
    // Getters

    public Long          getId()              { return id; }
    public String        getMarque()          { return marque; }
    public String        getModele()          { return modele; }
    public String        getImmatriculation() { return immatriculation; }
    public Categorie     getCategorie()       { return categorie; }
    public Carburant     getCarburant()       { return carburant; }
    public BoiteVitesse  getBoite()           { return boite; }
    public int           getPlaces()          { return places; }
    public BigDecimal    getTarifJournalier() { return tarifJournalier; }
    public int           getKilometrage()     { return kilometrage; }
    public String        getPhoto()           { return photo; }
    public StatutVehicule getStatut()         { return statut; }
    public List<Reservation>  getReservations()  { return reservations; }
    public List<Location>     getLocations()     { return locations; }
    public List<Maintenance>  getMaintenances()  { return maintenances; }

    // Setters
    public void setId(Long id)                              { this.id = id; }
    public void setMarque(String marque)                    { this.marque = marque; }
    public void setModele(String modele)                    { this.modele = modele; }
    public void setImmatriculation(String immatriculation)  { this.immatriculation = immatriculation; }
    public void setCategorie(Categorie categorie)           { this.categorie = categorie; }
    public void setCarburant(Carburant carburant)           { this.carburant = carburant; }
    public void setBoite(BoiteVitesse boite)                { this.boite = boite; }
    public void setPlaces(int places)                       { this.places = places; }
    public void setTarifJournalier(BigDecimal tarif)        { this.tarifJournalier = tarif; }
    public void setKilometrage(int kilometrage)             { this.kilometrage = kilometrage; }
    public void setPhoto(String photo)                      { this.photo = photo; }
    public void setStatut(StatutVehicule statut)            { this.statut = statut; }
    public void setReservations(List<Reservation> r)        { this.reservations = r; }
    public void setLocations(List<Location> l)              { this.locations = l; }
    public void setMaintenances(List<Maintenance> m)        { this.maintenances = m; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicule)) return false;
        return Objects.equals(id, ((Vehicule) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }

    @Override public String toString() {
        return "Vehicule{id=" + id + ", immatriculation='" + immatriculation + "', statut=" + statut + "}";
    }
}
