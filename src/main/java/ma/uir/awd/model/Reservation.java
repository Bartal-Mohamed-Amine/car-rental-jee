package ma.uir.awd.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A reservation made by a customer for a vehicle over a date range (R10).
 *
 * Key design decisions (AD-04, AD-05):
 * - Snapshot fields: dailyRateSnapshot and equipmentDailyTotalSnapshot are copied at booking time.
 *   Later price changes do NOT retroactively change history.
 * - Many-to-many with Equipement through reservation_equipement join table (T5, R8).
 * - Status flow: CONFIRMED -> PICKED_UP -> RETURNED or CONFIRMED -> CANCELLED.
 * - Conflict detection (R11) uses: existing.start <= req.end AND existing.end >= req.start,
 *   with active statuses = {CONFIRMED, PICKED_UP}.
 */
@Entity
@Table(name = "reservation",
    indexes = {
        @Index(name = "idx_reservation_vehicule_dates", columnList = "vehicule_id, date_debut, date_fin"),
        @Index(name = "idx_reservation_client",         columnList = "client_id"),
        @Index(name = "idx_reservation_statut",         columnList = "statut")
    }
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Utilisateur client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    /**
     * Snapshot of the daily rate at time of booking.
     * This preserves invoice integrity even if the vehicle's rate changes later.
     */
    @Column(name = "daily_rate_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRateSnapshot;

    /**
     * Snapshot of the total daily cost of all selected equipment at booking time.
     */
    @Column(name = "equipment_daily_total_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal equipmentDailyTotalSnapshot = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatutReservation statut = StatutReservation.CONFIRMED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Optional location that is created at pick-up (R12). */
    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private Location location;

    /** Many-to-many with optional equipment (R8, T5). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reservation_equipement",
        joinColumns        = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "equipement_id")
    )
    private Set<Equipement> equipements = new HashSet<>();

    // -------------------------------------------------------------------------

    public Reservation() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Getters

    public Long              getId()                         { return id; }
    public Utilisateur       getClient()                     { return client; }
    public Vehicule          getVehicule()                   { return vehicule; }
    public LocalDate         getDateDebut()                  { return dateDebut; }
    public LocalDate         getDateFin()                    { return dateFin; }
    public BigDecimal        getDailyRateSnapshot()          { return dailyRateSnapshot; }
    public BigDecimal        getEquipmentDailyTotalSnapshot(){ return equipmentDailyTotalSnapshot; }
    public StatutReservation getStatut()                     { return statut; }
    public LocalDateTime     getCreatedAt()                  { return createdAt; }
    public Location          getLocation()                   { return location; }
    public Set<Equipement>   getEquipements()                { return equipements; }

    // Setters
    public void setId(Long id)                                       { this.id = id; }
    public void setClient(Utilisateur client)                        { this.client = client; }
    public void setVehicule(Vehicule vehicule)                       { this.vehicule = vehicule; }
    public void setDateDebut(LocalDate dateDebut)                    { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin)                        { this.dateFin = dateFin; }
    public void setDailyRateSnapshot(BigDecimal d)                   { this.dailyRateSnapshot = d; }
    public void setEquipmentDailyTotalSnapshot(BigDecimal e)         { this.equipmentDailyTotalSnapshot = e; }
    public void setStatut(StatutReservation statut)                  { this.statut = statut; }
    public void setCreatedAt(LocalDateTime createdAt)                { this.createdAt = createdAt; }
    public void setLocation(Location location)                       { this.location = location; }
    public void setEquipements(Set<Equipement> equipements)          { this.equipements = equipements; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        return Objects.equals(id, ((Reservation) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }

    @Override public String toString() {
        return "Reservation{id=" + id + ", statut=" + statut + ", debut=" + dateDebut + ", fin=" + dateFin + "}";
    }
}
