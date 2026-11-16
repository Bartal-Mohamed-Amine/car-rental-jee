package ma.uir.awd.metier;

import ma.uir.awd.dao.ITransactionRunner;
import ma.uir.awd.dao.IEquipementDao;
import ma.uir.awd.dao.IReservationDao;
import ma.uir.awd.dao.IVehiculeDao;
import ma.uir.awd.metier.impl.ReservationMetierImpl;
import ma.uir.awd.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for reservation conflict detection business rules (R11, T4).
 * Uses in-memory test doubles (no Mockito, no DB required).
 */
public class ReservationConflictTest {

    private MockReservationDao reservationDao;
    private MockVehiculeDao vehiculeDao;
    private MockEquipementDao equipementDao;
    private ReservationMetierImpl metier;

    private static final LocalDate FIXED_TODAY = LocalDate.of(2026, 6, 10);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-06-10T10:00:00Z"), ZoneId.of("UTC")
    );

    private Vehicule availableVehicule;
    private Vehicule maintenanceVehicule;
    private Utilisateur client;

    @BeforeEach
    void setUp() {
        reservationDao = new MockReservationDao();
        vehiculeDao = new MockVehiculeDao();
        equipementDao = new MockEquipementDao();

        ITransactionRunner directTx = new ITransactionRunner() {
            @Override
            public <T> T inTransaction(Supplier<T> work) {
                return work.get();
            }
        };

        metier = new ReservationMetierImpl(reservationDao, vehiculeDao, equipementDao, directTx, FIXED_CLOCK);

        Categorie cat = new Categorie("Citadine", "Petite citadine économique");
        cat.setId(1L);

        availableVehicule = new Vehicule();
        availableVehicule.setId(1L);
        availableVehicule.setMarque("Renault");
        availableVehicule.setModele("Clio 5");
        availableVehicule.setImmatriculation("12345-A-1");
        availableVehicule.setCategorie(cat);
        availableVehicule.setStatut(StatutVehicule.AVAILABLE);
        availableVehicule.setTarifJournalier(new BigDecimal("350.00"));
        vehiculeDao.save(availableVehicule);

        maintenanceVehicule = new Vehicule();
        maintenanceVehicule.setId(2L);
        maintenanceVehicule.setMarque("Dacia");
        maintenanceVehicule.setModele("Duster");
        maintenanceVehicule.setImmatriculation("99999-B-2");
        maintenanceVehicule.setCategorie(cat);
        maintenanceVehicule.setStatut(StatutVehicule.MAINTENANCE);
        maintenanceVehicule.setTarifJournalier(new BigDecimal("400.00"));
        vehiculeDao.save(maintenanceVehicule);

        client = new Utilisateur("Test Client", "client@test.ma", "hash", "0612345678", Role.CLIENT);
        client.setId(10L);
    }

    @Test
    @DisplayName("1. Same-day turnaround (return day == new pickup day) is ALLOWED")
    void testSameDayTurnaround() {
        // Existing reservation: June 12 to June 15
        Reservation existing = createReservation(101L, availableVehicule,
                LocalDate.of(2026, 6, 12), LocalDate.of(2026, 6, 15), StatutReservation.CONFIRMED);
        reservationDao.save(existing);

        // New booking starts on June 15 (same day as previous return)
        LocalDate newStart = LocalDate.of(2026, 6, 15);
        LocalDate newEnd = LocalDate.of(2026, 6, 18);

        assertDoesNotThrow(() -> {
            Reservation r = metier.reserver(client.getId(), availableVehicule.getId(), newStart, newEnd, Collections.emptySet());
            assertNotNull(r);
            assertEquals(newStart, r.getDateDebut());
            assertEquals(newEnd, r.getDateFin());
        });
    }

    @Test
    @DisplayName("2. Nested period (new booking entirely inside existing) is REJECTED")
    void testNestedPeriods() {
        // Existing reservation: June 12 to June 20
        Reservation existing = createReservation(102L, availableVehicule,
                LocalDate.of(2026, 6, 12), LocalDate.of(2026, 6, 20), StatutReservation.CONFIRMED);
        reservationDao.save(existing);

        // New booking: June 14 to June 18 (nested inside existing)
        LocalDate newStart = LocalDate.of(2026, 6, 14);
        LocalDate newEnd = LocalDate.of(2026, 6, 18);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                metier.reserver(client.getId(), availableVehicule.getId(), newStart, newEnd, Collections.emptySet())
        );
        assertEquals("error.vehicle.unavailable", ex.getKey());
    }

    @Test
    @DisplayName("3. Identical period (exact same dates) is REJECTED")
    void testIdenticalPeriods() {
        // Existing reservation: June 12 to June 16
        Reservation existing = createReservation(103L, availableVehicule,
                LocalDate.of(2026, 6, 12), LocalDate.of(2026, 6, 16), StatutReservation.CONFIRMED);
        reservationDao.save(existing);

        // New booking: June 12 to June 16 (identical)
        LocalDate newStart = LocalDate.of(2026, 6, 12);
        LocalDate newEnd = LocalDate.of(2026, 6, 16);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                metier.reserver(client.getId(), availableVehicule.getId(), newStart, newEnd, Collections.emptySet())
        );
        assertEquals("error.vehicle.unavailable", ex.getKey());
    }

    @Test
    @DisplayName("4. Adjacent period (existing ends day before new starts) is ALLOWED")
    void testAdjacentPeriods() {
        // Existing reservation: June 12 to June 15
        Reservation existing = createReservation(104L, availableVehicule,
                LocalDate.of(2026, 6, 12), LocalDate.of(2026, 6, 15), StatutReservation.CONFIRMED);
        reservationDao.save(existing);

        // New booking: June 16 to June 20 (adjacent day)
        LocalDate newStart = LocalDate.of(2026, 6, 16);
        LocalDate newEnd = LocalDate.of(2026, 6, 20);

        assertDoesNotThrow(() -> {
            Reservation r = metier.reserver(client.getId(), availableVehicule.getId(), newStart, newEnd, Collections.emptySet());
            assertNotNull(r);
        });
    }

    @Test
    @DisplayName("5. Vehicle in MAINTENANCE status is REJECTED")
    void testMaintenanceVehicleBlocked() {
        LocalDate newStart = LocalDate.of(2026, 6, 12);
        LocalDate newEnd = LocalDate.of(2026, 6, 15);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                metier.reserver(client.getId(), maintenanceVehicule.getId(), newStart, newEnd, Collections.emptySet())
        );
        assertEquals("error.vehicle.maintenance", ex.getKey());
    }

    @Test
    @DisplayName("6. CANCELLED reservations are IGNORED during conflict check")
    void testCancelledReservationIgnored() {
        // Existing reservation on exact same dates but CANCELLED
        Reservation cancelled = createReservation(105L, availableVehicule,
                LocalDate.of(2026, 6, 12), LocalDate.of(2026, 6, 16), StatutReservation.CANCELLED);
        reservationDao.save(cancelled);

        LocalDate newStart = LocalDate.of(2026, 6, 12);
        LocalDate newEnd = LocalDate.of(2026, 6, 16);

        assertDoesNotThrow(() -> {
            Reservation r = metier.reserver(client.getId(), availableVehicule.getId(), newStart, newEnd, Collections.emptySet());
            assertNotNull(r);
            assertEquals(StatutReservation.CONFIRMED, r.getStatut());
        });
    }

    @Test
    @DisplayName("7. Overdue rental (PICKED_UP past planned end date) BLOCKS new bookings starting today")
    void testOverdueRentalBlocksNewBooking() {
        // Fixed today is June 10, 2026.
        // Existing rental was planned June 1 to June 8 (2 days overdue today), still PICKED_UP
        Reservation overdue = createReservation(106L, availableVehicule,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 8), StatutReservation.PICKED_UP);
        reservationDao.save(overdue);

        // New booking requested starting today (June 10 to June 14)
        LocalDate newStart = LocalDate.of(2026, 6, 10);
        LocalDate newEnd = LocalDate.of(2026, 6, 14);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                metier.reserver(client.getId(), availableVehicule.getId(), newStart, newEnd, Collections.emptySet())
        );
        assertEquals("error.vehicle.unavailable", ex.getKey());
    }

    private Reservation createReservation(Long id, Vehicule v, LocalDate start, LocalDate end, StatutReservation statut) {
        Reservation r = new Reservation();
        r.setId(id);
        r.setVehicule(v);
        r.setClient(client);
        r.setDateDebut(start);
        r.setDateFin(end);
        r.setDailyRateSnapshot(v.getTarifJournalier());
        r.setEquipmentDailyTotalSnapshot(BigDecimal.ZERO);
        r.setStatut(statut);
        return r;
    }

    // --- In-memory Test Doubles ---

    static class MockReservationDao implements IReservationDao {
        private final Map<Long, Reservation> store = new HashMap<>();
        private long seq = 200L;

        @Override
        public Reservation findById(Long id) {
            return store.get(id);
        }

        @Override
        public List<Reservation> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public void save(Reservation r) {
            if (r.getId() == null) r.setId(++seq);
            store.put(r.getId(), r);
        }

        @Override
        public void update(Reservation r) {
            store.put(r.getId(), r);
        }

        @Override
        public void saveOrUpdate(Reservation r) {
            save(r);
        }

        @Override
        public void delete(Reservation r) {
            store.remove(r.getId());
        }

        @Override
        public List<Reservation> findByClient(Long clientId) {
            List<Reservation> res = new ArrayList<>();
            for (Reservation r : store.values()) {
                if (r.getClient() != null && Objects.equals(r.getClient().getId(), clientId)) {
                    res.add(r);
                }
            }
            return res;
        }

        @Override
        public List<Reservation> findActiveByVehicule(Long vehiculeId) {
            List<Reservation> res = new ArrayList<>();
            for (Reservation r : store.values()) {
                if (r.getVehicule() != null && Objects.equals(r.getVehicule().getId(), vehiculeId)) {
                    if (r.getStatut() == StatutReservation.CONFIRMED || r.getStatut() == StatutReservation.PICKED_UP) {
                        res.add(r);
                    }
                }
            }
            return res;
        }

        @Override
        public List<Reservation> findCompletedForTraining() {
            List<Reservation> res = new ArrayList<>();
            for (Reservation r : store.values()) {
                if (r.getStatut() == StatutReservation.RETURNED || r.getStatut() == StatutReservation.CANCELLED) {
                    res.add(r);
                }
            }
            return res;
        }
    }

    static class MockVehiculeDao implements IVehiculeDao {
        private final Map<Long, Vehicule> store = new HashMap<>();

        @Override public Vehicule findById(Long id) { return store.get(id); }
        @Override public Vehicule findByIdForUpdate(Long id) { return store.get(id); }
        @Override public List<Vehicule> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Vehicule> findAllWithCategorie() { return new ArrayList<>(store.values()); }
        @Override public void save(Vehicule entity) { store.put(entity.getId(), entity); }
        @Override public void update(Vehicule entity) { store.put(entity.getId(), entity); }
        @Override public void saveOrUpdate(Vehicule entity) { save(entity); }
        @Override public void delete(Vehicule entity) { store.remove(entity.getId()); }
        @Override public Vehicule findByImmatriculation(String immatriculation) { return null; }
        @Override public List<Vehicule> findByStatut(StatutVehicule statut) { return Collections.emptyList(); }
        @Override public List<Vehicule> findByCategorie(Long categorieId) { return Collections.emptyList(); }
    }

    static class MockEquipementDao implements IEquipementDao {
        private final Map<Long, Equipement> store = new HashMap<>();

        @Override public Equipement findById(Long id) { return store.get(id); }
        @Override public List<Equipement> findAll() { return new ArrayList<>(store.values()); }
        @Override public List<Equipement> findByIds(Set<Long> ids) {
            List<Equipement> res = new ArrayList<>();
            for (Long id : ids) {
                if (store.containsKey(id)) res.add(store.get(id));
            }
            return res;
        }
        @Override public void save(Equipement e) { store.put(e.getId(), e); }
        @Override public void update(Equipement e) { store.put(e.getId(), e); }
        @Override public void saveOrUpdate(Equipement e) { save(e); }
        @Override public void delete(Equipement e) { store.remove(e.getId()); }
        @Override public List<Equipement> findActive() { return new ArrayList<>(store.values()); }
    }
}
