package ma.uir.awd.metier.ml;

import ma.uir.awd.metier.IRiskMetier;
import ma.uir.awd.model.ModelMetrics;
import ma.uir.awd.model.ModelStatus;
import ma.uir.awd.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Skeleton implementation of IRiskMetier (R15, R18).
 * Lives in ma.uir.awd.metier.ml package.
 * Currently returns ModelStatus.NOT_TRAINED until the full ML training pipeline
 * is executed. Includes the model persistence infrastructure for saving and loading
 * serialized models to/from the external model directory.
 */
public class RiskMetierImpl implements IRiskMetier {

    private static final Logger log = LoggerFactory.getLogger(RiskMetierImpl.class);

    private final String modelDir;
    private volatile ModelStatus modelStatus = ModelStatus.NOT_TRAINED;
    private volatile ModelMetrics currentMetrics;

    public RiskMetierImpl(String modelDir) {
        this.modelDir = modelDir;
        loadLatestModel();
    }

    /**
     * Attempts to reload an existing serialized model from the filesystem on startup.
     * If absent, remains in NOT_TRAINED state.
     */
    public synchronized void loadLatestModel() {
        try {
            Path dir = Paths.get(modelDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path latestPointer = dir.resolve("latest-model.bin");
            if (Files.exists(latestPointer)) {
                try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(latestPointer))) {
                    Object loaded = ois.readObject();
                    if (loaded != null) {
                        this.modelStatus = ModelStatus.TRAINED;
                        log.info("Loaded persisted risk model from {}", latestPointer);
                    }
                }
            } else {
                this.modelStatus = ModelStatus.NOT_TRAINED;
                log.info("No persisted model found at {}. Status remains NOT_TRAINED.", latestPointer);
            }
        } catch (Exception e) {
            log.warn("Could not load model from {}: {}", modelDir, e.getMessage());
            this.modelStatus = ModelStatus.NOT_TRAINED;
        }
    }

    /**
     * Persist a trained model object to disk (R18).
     */
    public synchronized void persistModel(Serializable model, String filename) throws IOException {
        Path targetPath = Paths.get(modelDir, filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(targetPath))) {
            oos.writeObject(model);
        }
        Path latestPointer = Paths.get(modelDir, "latest-model.bin");
        Files.deleteIfExists(latestPointer);
        Files.copy(targetPath, latestPointer);
        this.modelStatus = ModelStatus.TRAINED;
        log.info("Successfully persisted new model to {} and updated latest-model.bin", targetPath);
    }

    @Override
    public RiskPrediction predict(Reservation reservation) {
        if (modelStatus == ModelStatus.NOT_TRAINED) {
            return RiskPrediction.notTrained();
        }
        // When trained, actual model inference takes place here
        return new RiskPrediction(0.15, "LOW", true);
    }

    @Override
    public ModelStatus status() {
        return modelStatus;
    }

    @Override
    public ModelMetrics latestMetrics() {
        return currentMetrics;
    }

    public void setCurrentMetrics(ModelMetrics metrics) {
        this.currentMetrics = metrics;
    }
}
