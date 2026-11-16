package ma.uir.awd.metier;

import ma.uir.awd.dao.HibernateUtil;
import ma.uir.awd.dao.IEquipementDao;
import ma.uir.awd.dao.IReservationDao;
import ma.uir.awd.dao.ITransactionRunner;
import ma.uir.awd.dao.IVehiculeDao;
import ma.uir.awd.dao.impl.EquipementDaoImpl;
import ma.uir.awd.dao.impl.ReservationDaoImpl;
import ma.uir.awd.dao.impl.TransactionRunnerImpl;
import ma.uir.awd.dao.impl.VehiculeDaoImpl;
import ma.uir.awd.metier.impl.ReservationMetierImpl;
import ma.uir.awd.model.Reservation;
import ma.uir.awd.model.StatutReservation;
import ma.uir.awd.model.Vehicule;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Concurrency integration test verifying that PESSIMISTIC_WRITE lock on the vehicle row
 * prevents double-booking when two threads attempt to reserve the same vehicle for the
 * same dates simultaneously (R11, AD-04).
 *
 * Runs against the live MySQL 8 database started via Docker Compose on port 3307.
 */
@Tag("integration")
public class ReservationConcurrencyIT {

    private static IReservationMetier metier;
    private static IReservationDao reservationDao;
    private static IVehiculeDao vehiculeDao;
    private static ITransactionRunner tx;
    private static boolean dbAvailable = false;

    private static final Long TEST_VEHICULE_ID = 1L; // Clio 5 from dev-seed.sql
    private static final Long CLIENT_ID_1 = 3L;     // Omar Tazi (CLIENT)
    private static final Long CLIENT_ID_2 = 3L;
    private static final LocalDate START_DATE = LocalDate.of(2027, 3, 15);
    private static final LocalDate END_DATE = LocalDate.of(2027, 3, 20);

    @BeforeAll
    static void setUpAll() {
        try {
            HibernateUtil.init();
            tx = new TransactionRunnerImpl();
            reservationDao = new ReservationDaoImpl();
            vehiculeDao = new VehiculeDaoImpl();
            IEquipementDao equipementDao = new EquipementDaoImpl();
            metier = new ReservationMetierImpl(reservationDao, vehiculeDao, equipementDao, tx);

            // Verify connection works
            Vehicule v = tx.inTransaction(() -> vehiculeDao.findById(TEST_VEHICULE_ID));
            dbAvailable = (v != null);
        } catch (Exception e) {
            System.err.println("Database not reachable for integration test: " + e.getMessage());
            dbAvailable = false;
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (dbAvailable) {
            HibernateUtil.shutdown();
        }
    }

    @BeforeEach
    @AfterEach
    void cleanUpTestReservations() {
        if (!dbAvailable) return;

        tx.inTransaction(() -> {
            List<Reservation> list = reservationDao.findActiveByVehicule(TEST_VEHICULE_ID);
            for (Reservation r : list) {
                if (START_DATE.equals(r.getDateDebut()) && END_DATE.equals(r.getDateFin())) {
                    r.setStatut(StatutReservation.CANCELLED);
                    reservationDao.update(r);
                }
            }
        });
    }

    @Test
    @DisplayName("PESSIMISTIC_WRITE lock ensures exactly one of two concurrent bookings succeeds")
    void testConcurrentDoubleBookingExactlyOneSucceeds() throws Exception {
        assumeTrue(dbAvailable, "MySQL database is not reachable on configured port; skipping integration test.");

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        List<Reservation> created = Collections.synchronizedList(new ArrayList<>());

        Callable<Void> bookingTask = () -> {
            readyLatch.countDown();
            // Block until both threads are ready, ensuring concurrent execution
            startLatch.await();
            try {
                Reservation res = metier.reserver(CLIENT_ID_1, TEST_VEHICULE_ID, START_DATE, END_DATE, Collections.emptySet());
                successCount.incrementAndGet();
                created.add(res);
            } catch (BusinessException be) {
                if ("error.vehicle.unavailable".equals(be.getKey())) {
                    conflictCount.incrementAndGet();
                }
                exceptions.add(be);
            } catch (Throwable t) {
                exceptions.add(t);
            }
            return null;
        };

        Future<Void> f1 = executor.submit(bookingTask);
        Future<Void> f2 = executor.submit(bookingTask);

        // Wait for both threads to reach start line
        readyLatch.await(5, TimeUnit.SECONDS);
        // Release start line simultaneously
        startLatch.countDown();

        f1.get(15, TimeUnit.SECONDS);
        f2.get(15, TimeUnit.SECONDS);
        executor.shutdown();

        // Assertions:
        // Exactly one booking succeeded and one was rejected due to conflict
        assertEquals(1, successCount.get(), "Exactly one reservation must succeed");
        assertEquals(1, conflictCount.get(), "Exactly one reservation must fail with vehicle unavailable");
        assertEquals(1, created.size(), "Exactly one reservation record was created");

        assertNotNull(created.get(0).getId());
        assertEquals(StatutReservation.CONFIRMED, created.get(0).getStatut());
    }
}
