package ma.uir.awd.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Invoice generated automatically at vehicle return (R13).
 * Every component of the total is stored separately for transparency.
 *
 * Formula (AD-05 decisions.md):
 *   base          = rentalDays × dailyRateSnapshot
 *   equipment     = rentalDays × equipmentDailyTotalSnapshot
 *   lateFee       = lateDays × dailyRateSnapshot × lateFeeRate
 *   subtotal      = base + equipment + lateFee
 *   vatAmount     = subtotal × vatRate
 *   total         = subtotal + vatAmount
 *
 * vatRate and lateFeeRate are copied from Parametres at the time the invoice is generated,
 * so admin rate changes do not alter existing invoices.
 */
@Entity
@Table(name = "facture",
    indexes = { @Index(name = "idx_facture_location", columnList = "location_id") }
)
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false, unique = true)
    private Location location;

    @Column(name = "rental_days", nullable = false)
    private int rentalDays;

    @Column(name = "late_days", nullable = false)
    private int lateDays;

    @Column(name = "base_amount",      nullable = false, precision = 12, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "equipment_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal equipmentAmount;

    @Column(name = "late_fee_amount",  nullable = false, precision = 12, scale = 2)
    private BigDecimal lateFeeAmount;

    @Column(name = "subtotal",         nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "vat_rate",         nullable = false, precision = 6, scale = 4)
    private BigDecimal vatRate;        // snapshot of Parametres.vatRate

    @Column(name = "vat_amount",       nullable = false, precision = 12, scale = 2)
    private BigDecimal vatAmount;

    @Column(name = "total",            nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "late_fee_rate",    nullable = false, precision = 6, scale = 4)
    private BigDecimal lateFeeRate;    // snapshot of Parametres.lateFeeRate

    @Column(name = "emitted_at", nullable = false)
    private LocalDateTime emittedAt;

    // -------------------------------------------------------------------------

    public Facture() {}

    @PrePersist
    protected void onCreate() {
        if (emittedAt == null) emittedAt = LocalDateTime.now();
    }

    // Getters
    public Long        getId()              { return id; }
    public Location    getLocation()        { return location; }
    public int         getRentalDays()      { return rentalDays; }
    public int         getLateDays()        { return lateDays; }
    public BigDecimal  getBaseAmount()      { return baseAmount; }
    public BigDecimal  getEquipmentAmount() { return equipmentAmount; }
    public BigDecimal  getLateFeeAmount()   { return lateFeeAmount; }
    public BigDecimal  getSubtotal()        { return subtotal; }
    public BigDecimal  getVatRate()         { return vatRate; }
    public BigDecimal  getVatAmount()       { return vatAmount; }
    public BigDecimal  getTotal()           { return total; }
    public BigDecimal  getLateFeeRate()     { return lateFeeRate; }
    public LocalDateTime getEmittedAt()     { return emittedAt; }

    // Setters
    public void setId(Long id)                     { this.id = id; }
    public void setLocation(Location location)     { this.location = location; }
    public void setRentalDays(int rentalDays)      { this.rentalDays = rentalDays; }
    public void setLateDays(int lateDays)          { this.lateDays = lateDays; }
    public void setBaseAmount(BigDecimal b)        { this.baseAmount = b; }
    public void setEquipmentAmount(BigDecimal e)   { this.equipmentAmount = e; }
    public void setLateFeeAmount(BigDecimal l)     { this.lateFeeAmount = l; }
    public void setSubtotal(BigDecimal s)          { this.subtotal = s; }
    public void setVatRate(BigDecimal v)           { this.vatRate = v; }
    public void setVatAmount(BigDecimal v)         { this.vatAmount = v; }
    public void setTotal(BigDecimal t)             { this.total = t; }
    public void setLateFeeRate(BigDecimal l)       { this.lateFeeRate = l; }
    public void setEmittedAt(LocalDateTime e)      { this.emittedAt = e; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facture)) return false;
        return Objects.equals(id, ((Facture) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
