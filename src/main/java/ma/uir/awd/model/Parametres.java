package ma.uir.awd.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Global application settings singleton (R2).
 * Only one row exists (id = 1). Admin can update VAT rate and late-fee multiplier.
 *
 * Both rates are stored as fractions:
 *   vatRate = 0.20 means 20% VAT
 *   lateFeeRate = 1.5 means 150% of daily rate per late day
 */
@Entity
@Table(name = "parametres")
public class Parametres {

    /** Always id=1 — there is exactly one row. */
    @Id
    private Long id = 1L;

    /** VAT fraction, e.g. 0.2000 for 20% */
    @Column(name = "vat_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal vatRate;

    /**
     * Late-fee multiplier, e.g. 1.5 means each late day costs 1.5× the daily rate.
     * This is a business rule decided in AD-05; documented here for the defence.
     */
    @Column(name = "late_fee_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal lateFeeRate;

    // -------------------------------------------------------------------------

    public Parametres() {}

    public Parametres(BigDecimal vatRate, BigDecimal lateFeeRate) {
        this.vatRate     = vatRate;
        this.lateFeeRate = lateFeeRate;
    }

    public Long       getId()          { return id; }
    public BigDecimal getVatRate()     { return vatRate; }
    public BigDecimal getLateFeeRate() { return lateFeeRate; }

    public void setId(Long id)                   { this.id = id; }
    public void setVatRate(BigDecimal vatRate)    { this.vatRate = vatRate; }
    public void setLateFeeRate(BigDecimal l)     { this.lateFeeRate = l; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parametres)) return false;
        return Objects.equals(id, ((Parametres) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
