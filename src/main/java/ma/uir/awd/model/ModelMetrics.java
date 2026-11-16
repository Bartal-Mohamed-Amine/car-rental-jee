package ma.uir.awd.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ML model training run history (R18).
 * Each retrain inserts one row; the admin page shows the latest metrics table.
 */
@Entity
@Table(name = "model_metrics",
    indexes = { @Index(name = "idx_model_metrics_trained_at", columnList = "trained_at") }
)
public class ModelMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trained_at", nullable = false)
    private LocalDateTime trainedAt;

    @Column(name = "model_type", nullable = false, length = 80)
    private String modelType;       // e.g. "RandomForest", "LogisticRegression"

    @Column(name = "model_file", length = 255)
    private String modelFile;       // filename saved to model.dir

    @Column(name = "train_size", nullable = false)
    private int trainSize;

    @Column(name = "test_size", nullable = false)
    private int testSize;

    // Model metrics
    @Column(precision = 6, scale = 4)
    private BigDecimal accuracy;

    @Column(name = "precision_score", precision = 6, scale = 4)
    private BigDecimal precision;

    @Column(precision = 6, scale = 4)
    private BigDecimal recall;

    @Column(name = "f1_score", precision = 6, scale = 4)
    private BigDecimal f1Score;

    // Baseline metrics for comparison (R17)
    @Column(name = "baseline_majority_accuracy", precision = 6, scale = 4)
    private BigDecimal baselineMajorityAccuracy;

    @Column(name = "baseline_rule_f1", precision = 6, scale = 4)
    private BigDecimal baselineRuleF1;

    @Column(name = "notes", length = 500)
    private String notes;

    // -------------------------------------------------------------------------

    public ModelMetrics() {}

    @PrePersist
    protected void onCreate() {
        if (trainedAt == null) trainedAt = LocalDateTime.now();
    }

    public Long         getId()                       { return id; }
    public LocalDateTime getTrainedAt()               { return trainedAt; }
    public String       getModelType()                { return modelType; }
    public String       getModelFile()                { return modelFile; }
    public int          getTrainSize()                { return trainSize; }
    public int          getTestSize()                 { return testSize; }
    public BigDecimal   getAccuracy()                 { return accuracy; }
    public BigDecimal   getPrecision()                { return precision; }
    public BigDecimal   getRecall()                   { return recall; }
    public BigDecimal   getF1Score()                  { return f1Score; }
    public BigDecimal   getBaselineMajorityAccuracy() { return baselineMajorityAccuracy; }
    public BigDecimal   getBaselineRuleF1()           { return baselineRuleF1; }
    public String       getNotes()                    { return notes; }

    public void setId(Long id)                                        { this.id = id; }
    public void setTrainedAt(LocalDateTime trainedAt)                 { this.trainedAt = trainedAt; }
    public void setModelType(String modelType)                        { this.modelType = modelType; }
    public void setModelFile(String modelFile)                        { this.modelFile = modelFile; }
    public void setTrainSize(int trainSize)                           { this.trainSize = trainSize; }
    public void setTestSize(int testSize)                             { this.testSize = testSize; }
    public void setAccuracy(BigDecimal accuracy)                      { this.accuracy = accuracy; }
    public void setPrecision(BigDecimal precision)                    { this.precision = precision; }
    public void setRecall(BigDecimal recall)                          { this.recall = recall; }
    public void setF1Score(BigDecimal f1Score)                        { this.f1Score = f1Score; }
    public void setBaselineMajorityAccuracy(BigDecimal b)             { this.baselineMajorityAccuracy = b; }
    public void setBaselineRuleF1(BigDecimal b)                       { this.baselineRuleF1 = b; }
    public void setNotes(String notes)                                { this.notes = notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelMetrics)) return false;
        return Objects.equals(id, ((ModelMetrics) o).id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
