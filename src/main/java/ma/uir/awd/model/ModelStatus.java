package ma.uir.awd.model;

/**
 * ML model lifecycle status (R18).
 * NOT_TRAINED: no persisted model exists yet; UI shows a neutral message.
 * TRAINED:     a model file is loaded and predictions are available.
 */
public enum ModelStatus {
    NOT_TRAINED, TRAINED
}
