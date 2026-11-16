package ma.uir.awd.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Vehicle maintenance log entry (R7).
 * A vehicle under maintenance cannot be reserved (conflict detection checks StatutVehicule.MAINTENANCE).
 */
@Entity
@Table(name = "maintenance",
    indexes = { @Index(name = "idx_maintenance_vehicule", columnList = "vehicule_id") }
)
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(name = "date_operation", nullable = false)
    private LocalDate dateOperation;

    @Column(nullable = false, length = 100)
    private String type;    // e.g. "Vidange", "Révision générale", "Carrosserie"

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cout;

    @Column(nullable = false)
    private int kilometrage;  // mileage at the time of maintenance

    // -------------------------------------------------------------------------

    public Maintenance() {}

    public Long       getId()          { return id; }
    public Vehicule   getVehicule()    { return vehicule; }
    public LocalDate  getDateOperation() { return dateOperation; }
    public LocalDate  getDate()          { return dateOperation; }
    public String     getType()          { return type; }
    public String     getDescription()   { return description; }
    public BigDecimal getCout()          { return cout; }
    public int        getKilometrage()   { return kilometrage; }

    public void setId(Long id)                               { this.id = id; }
    public void setVehicule(Vehicule vehicule)                { this.vehicule = vehicule; }
    public void setDateOperation(LocalDate dateOperation)    { this.dateOperation = dateOperation; }
    public void setDate(LocalDate date)                      { this.dateOperation = date; }
    public void setType(String type)             { this.type = type; }
    public void setDescription(String d)         { this.description = d; }
    public void setCout(BigDecimal cout)         { this.cout = cout; }
    public void setKilometrage(int kilometrage)  { this.kilometrage = kilometrage; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Maintenance)) return false;
        return Objects.equals(id, ((Maintenance) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
