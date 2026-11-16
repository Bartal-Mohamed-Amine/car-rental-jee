package ma.uir.awd.web.action;

import ma.uir.awd.metier.IVehiculeMetier;
import ma.uir.awd.model.Vehicule;
import ma.uir.awd.web.util.AppContext;

import java.util.List;

/**
 * Vertical slice action: Vehicle catalogue view accessible to all authenticated roles.
 * Displays vehicle list with category details and status badges (AVAILABLE, RENTED, MAINTENANCE).
 */
public class VehiculeAction extends BaseAction {

    private List<Vehicule> vehicules;

    @Override
    public String execute() {
        IVehiculeMetier vehiculeMetier = AppContext.get(IVehiculeMetier.class);
        this.vehicules = vehiculeMetier.listAll();
        return SUCCESS;
    }

    public List<Vehicule> getVehicules() {
        return vehicules;
    }
}
