package ma.uir.awd.model;

/**
 * Vehicle availability status (R6).
 * - AVAILABLE: can be reserved
 * - RENTED:    currently on a confirmed rental (PICKED_UP)
 * - MAINTENANCE: blocked from booking, set manually by admin/agent (R30 of brief)
 *
 * Status transitions:
 *   AVAILABLE -> RENTED (at pick-up)
 *   RENTED -> AVAILABLE (at return)
 *   AVAILABLE | RENTED -> MAINTENANCE (admin/agent, only if no active rental)
 *   MAINTENANCE -> AVAILABLE (admin/agent)
 */
public enum StatutVehicule {
    AVAILABLE, RENTED, MAINTENANCE
}
