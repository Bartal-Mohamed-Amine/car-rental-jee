package ma.uir.awd.model;

/**
 * Reservation lifecycle (R10, R12, R13).
 * CONFIRMED -> PICKED_UP -> RETURNED (normal flow)
 * CONFIRMED -> CANCELLED  (customer cancels before pick-up)
 */
public enum StatutReservation {
    CONFIRMED, PICKED_UP, RETURNED, CANCELLED
}
