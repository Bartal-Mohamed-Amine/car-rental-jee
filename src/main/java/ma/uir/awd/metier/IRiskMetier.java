package ma.uir.awd.metier;

import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.ModelStatus;
import ma.uir.awd.model.Reservation;

/**
 * ML risk interface (R15, R18, R19).
 *
 * Actions and JSPs only ever see this interface — no Smile or model class leaks
 * into the web layer. This is enforced by the audit script (R15 check).
 *
 * At startup, if no model file exists, status() returns NOT_TRAINED and the UI
 * shows a neutral "no model yet" message instead of crashing (AD-08).
 */
public interface IRiskMetier {

    /**
     * Predict the probability that a reservation will result in a late return or
     * cancellation. Returns a RiskPrediction with probability [0.0, 1.0] and label.
     * If the model is NOT_TRAINED, returns a neutral prediction.
     */
    RiskPrediction predict(Reservation reservation);

    /** Current state of the persisted model (NOT_TRAINED or TRAINED). */
    ModelStatus status();

    /** The most recent training metrics, or null if never trained. */
    ModelMetrics latestMetrics();

    // -------------------------------------------------------------------------
    // Inner class: prediction result visible to the web layer

    /**
     * Value object returned by predict().
     * The web layer renders this; it never imports the model implementation.
     */
    class RiskPrediction {
        private final double probability;  // [0.0, 1.0]
        private final String label;        // e.g. "HIGH", "MEDIUM", "LOW"
        private final boolean trained;

        public RiskPrediction(double probability, String label, boolean trained) {
            this.probability = probability;
            this.label       = label;
            this.trained     = trained;
        }

        /** Convenience factory for the not-trained case. */
        public static RiskPrediction notTrained() {
            return new RiskPrediction(0.0, "NOT_TRAINED", false);
        }

        public double  getProbability() { return probability; }
        public String  getLabel()       { return label; }
        public boolean isTrained()      { return trained; }

        /** Percentage rounded to 0 decimal places, for display in JSP. */
        public int getProbabilityPct() {
            return (int) Math.round(probability * 100);
        }
    }
}
