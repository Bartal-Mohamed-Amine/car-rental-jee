package ma.uir.awd.metier;

import ma.uir.awd.model.Vehicule;
import java.util.List;

/**
 * Vehicle catalogue queries (R5, R9).
 * Only the web layer depends on this interface.
 */
public interface IVehiculeMetier {

    /** All vehicles, with category eagerly loaded (safe to pass to JSPs). */
    List<Vehicule> listAll();

    /** Find a vehicle by id. Throws BusinessException if not found. */
    Vehicule findById(Long id);
}
