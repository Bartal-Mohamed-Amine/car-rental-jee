package ma.uir.awd.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rental contract created at pick-up (R12). Linked 1-to-1 to a Reservation.
 * Closed at return; triggers invoice creation (R13).
 *
 * References both pick-up agent and return agent separately (R3, R14).
 */
@Entity
@Table(name = "location",
    indexes = {
        @Index(name = "idx_location_reservation", columnList = "reservation_id"),
        @Index(name = "idx_location_agent_prise", columnList = "agent_prise_id")
    }
)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_prise_id", nullable = false)
    private Utilisateur agentPrise;   // agent who processed pick-up

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_retour_id")
    private Utilisateur agentRetour;  // agent who processed return (null until returned)

    @Column(name = "date_prise", nullable = false)
    private LocalDate datePrise;

    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDate dateRetourPrevue;

    @Column(name = "date_retour_effective")
    private LocalDate dateRetourEffective; // null until vehicle is returned

    @Column(name = "kilometrage_depart", nullable = false)
    private int kilometrageDepart;

    @Column(name = "kilometrage_retour")
    private Integer kilometrageRetour; // null until returned

    @Column(name = "niveau_carburant_depart", nullable = false, length = 20)
    private String niveauCarburantDepart;

    @Column(name = "niveau_carburant_retour", length = 20)
    private String niveauCarburantRetour; // null until returned

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Facture facture;

    // -------------------------------------------------------------------------

    public Location() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // Getters
    public Long        getId()                      { return id; }
    public Reservation getReservation()             { return reservation; }
    public Utilisateur getAgentPrise()              { return agentPrise; }
    public Utilisateur getAgentRetour()             { return agentRetour; }
    public LocalDate   getDatePrise()               { return datePrise; }
    public LocalDate   getDateRetourPrevue()        { return dateRetourPrevue; }
    public LocalDate   getDateRetourEffective()     { return dateRetourEffective; }
    public int         getKilometrageDepart()       { return kilometrageDepart; }
    public Integer     getKilometrageRetour()       { return kilometrageRetour; }
    public String      getNiveauCarburantDepart()   { return niveauCarburantDepart; }
    public String      getNiveauCarburantRetour()   { return niveauCarburantRetour; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public Facture     getFacture()                 { return facture; }

    // Setters
    public void setId(Long id)                                       { this.id = id; }
    public void setReservation(Reservation reservation)              { this.reservation = reservation; }
    public void setAgentPrise(Utilisateur agentPrise)                { this.agentPrise = agentPrise; }
    public void setAgentRetour(Utilisateur agentRetour)              { this.agentRetour = agentRetour; }
    public void setDatePrise(LocalDate datePrise)                    { this.datePrise = datePrise; }
    public void setDateRetourPrevue(LocalDate d)                     { this.dateRetourPrevue = d; }
    public void setDateRetourEffective(LocalDate d)                  { this.dateRetourEffective = d; }
    public void setKilometrageDepart(int k)                          { this.kilometrageDepart = k; }
    public void setKilometrageRetour(Integer k)                      { this.kilometrageRetour = k; }
    public void setNiveauCarburantDepart(String n)                   { this.niveauCarburantDepart = n; }
    public void setNiveauCarburantRetour(String n)                   { this.niveauCarburantRetour = n; }
    public void setCreatedAt(LocalDateTime createdAt)                { this.createdAt = createdAt; }
    public void setFacture(Facture facture)                          { this.facture = facture; }

    /** True when the vehicle has been returned (R13). */
    public boolean isReturned() { return dateRetourEffective != null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        return Objects.equals(id, ((Location) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
