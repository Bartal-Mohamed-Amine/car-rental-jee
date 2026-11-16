package ma.uir.awd.metier;

import ma.uir.awd.dao.ITransactionRunner;
import ma.uir.awd.dao.IVehiculeDao;
import ma.uir.awd.metier.impl.VehiculeMetierImpl;
import ma.uir.awd.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying vehicle catalogue business operations (R5, T1, T4).
 * Uses an in-memory test double (MockVehiculeDao) and transaction runner to
 * test metier business logic in complete isolation without external dependencies.
 */
public class VehiculeMetierTest {

    /** In-memory test double for IVehiculeDao (explainable, robust, no bytecode manipulation). */
    private static class MockVehiculeDao implements IVehiculeDao {
        final Map<Long, Vehicule> storage = new HashMap<>();

        @Override public void save(Vehicule entity) { storage.put(entity.getId(), entity); }
        @Override public void update(Vehicule entity) { storage.put(entity.getId(), entity); }
        @Override public void saveOrUpdate(Vehicule entity) { storage.put(entity.getId(), entity); }
        @Override public void delete(Vehicule entity) { storage.remove(entity.getId()); }
        @Override public Vehicule findById(Long id) { return storage.get(id); }
        @Override public List<Vehicule> findAll() { return new ArrayList<>(storage.values()); }
        @Override public Vehicule findByImmatriculation(String immatriculation) {
            return storage.values().stream().filter(v -> immatriculation.equals(v.getImmatriculation())).findFirst().orElse(null);
        }
        @Override public List<Vehicule> findByStatut(StatutVehicule statut) {
            return storage.values().stream().filter(v -> statut == v.getStatut()).toList();
        }
        @Override public List<Vehicule> findByCategorie(Long categorieId) {
            return storage.values().stream().filter(v -> v.getCategorie() != null && categorieId.equals(v.getCategorie().getId())).toList();
        }
        @Override public Vehicule findByIdForUpdate(Long id) { return storage.get(id); }
        @Override public List<Vehicule> findAllWithCategorie() { return new ArrayList<>(storage.values()); }
    }

    // Direct in-memory transaction runner for unit tests
    private final ITransactionRunner testTx = new ITransactionRunner() {
        @Override
        public <T> T inTransaction(Supplier<T> work) {
            return work.get();
        }
    };

    private MockVehiculeDao mockDao;
    private IVehiculeMetier vehiculeMetier;

    @BeforeEach
    void setUp() {
        mockDao = new MockVehiculeDao();
        vehiculeMetier = new VehiculeMetierImpl(mockDao, testTx);
    }

    @Test
    @DisplayName("listAll returns vehicles stored in the DAO")
    void testListAllVehicles() {
        Categorie cat = new Categorie("Berline", "Voiture confortable");
        cat.setId(1L);

        Vehicule v1 = new Vehicule();
        v1.setId(1L);
        v1.setMarque("Peugeot");
        v1.setModele("508");
        v1.setImmatriculation("12345-A-1");
        v1.setCategorie(cat);
        v1.setCarburant(Carburant.DIESEL);
        v1.setBoite(BoiteVitesse.AUTOMATIQUE);
        v1.setPlaces(5);
        v1.setTarifJournalier(new BigDecimal("450.00"));
        v1.setKilometrage(25000);
        v1.setStatut(StatutVehicule.AVAILABLE);

        mockDao.save(v1);

        List<Vehicule> results = vehiculeMetier.listAll();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Peugeot", results.get(0).getMarque());
        assertEquals(StatutVehicule.AVAILABLE, results.get(0).getStatut());
    }

    @Test
    @DisplayName("findById throws BusinessException when vehicle does not exist")
    void testFindByIdNotFoundThrowsException() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
            vehiculeMetier.findById(999L)
        );

        assertEquals("error.vehicle.notfound", ex.getKey());
    }

    @Test
    @DisplayName("findById throws BusinessException on null id parameter")
    void testFindByIdNullThrowsException() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
            vehiculeMetier.findById(null)
        );

        assertEquals("error.vehicle.notfound", ex.getKey());
    }
}
