package ma.uir.awd.metier.impl;

import ma.uir.awd.dao.IEquipementDao;
import ma.uir.awd.dao.IReservationDao;
import ma.uir.awd.dao.ITransactionRunner;
import ma.uir.awd.dao.IVehiculeDao;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IReservationMetier;
import ma.uir.awd.model.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of IReservationMetier (R10, R11, T4).
 * Handles conflict detection, pessimistic locking (AD-04), and snapshot creation.
 */
public class ReservationMetierImpl implements IReservationMetier {

    private final IReservationDao reservationDao;
    private final IVehiculeDao vehiculeDao;
    private final IEquipementDao equipementDao;
    private final ITransactionRunner tx;
    private final Clock clock;

    public ReservationMetierImpl(IReservationDao reservationDao,
                                 IVehiculeDao vehiculeDao,
                                 IEquipementDao equipementDao,
                                 ITransactionRunner tx) {
        this(reservationDao, vehiculeDao, equipementDao, tx, Clock.systemDefaultZone());
    }

    public ReservationMetierImpl(IReservationDao reservationDao,
                                 IVehiculeDao vehiculeDao,
                                 IEquipementDao equipementDao,
                                 ITransactionRunner tx,
                                 Clock clock) {
        this.reservationDao = reservationDao;
        this.vehiculeDao = vehiculeDao;
        this.equipementDao = equipementDao;
        this.tx = tx;
        this.clock = clock;
    }

    @Override
    public Reservation reserver(Long clientId, Long vehiculeId, LocalDate debut, LocalDate fin, Set<Long> equipementIds) {
        return tx.inTransaction(() -> {
            LocalDate today = LocalDate.now(clock);

            if (debut == null || fin == null || fin.isBefore(debut) || debut.isBefore(today)) {
                throw new BusinessException("error.reservation.dates");
            }

            // 1. Acquire PESSIMISTIC_WRITE lock on the vehicle row (R11, AD-04)
            Vehicule v = vehiculeDao.findByIdForUpdate(vehiculeId);
            if (v == null) {
                throw new BusinessException("error.vehicle.notfound");
            }

            // 2. Reject if vehicle is marked MAINTENANCE
            if (v.getStatut() == StatutVehicule.MAINTENANCE) {
                throw new BusinessException("error.vehicle.maintenance");
            }

            // 3. Conflict detection against active reservations (CONFIRMED and PICKED_UP)
            List<Reservation> actives = reservationDao.findActiveByVehicule(vehiculeId);
            for (Reservation existing : actives) {
                // If rental is overdue (PICKED_UP past planned end date), use max(plannedEnd, today)
                boolean isOverdue = existing.getStatut() == StatutReservation.PICKED_UP
                        && existing.getDateFin().isBefore(today);

                LocalDate effectiveEnd = isOverdue ? today : existing.getDateFin();

                // An overdue vehicle is physically unreturned and blocks any new booking starting today or earlier
                if (isOverdue && !debut.isAfter(today)) {
                    throw new BusinessException("error.vehicle.unavailable");
                }

                // Standard overlap check allowing same-day turnaround:
                // Existing [start1, end1] and requested [debut, fin] overlap if start1 < fin AND effectiveEnd > debut
                boolean overlaps = existing.getDateDebut().isBefore(fin) && effectiveEnd.isAfter(debut);
                if (overlaps) {
                    throw new BusinessException("error.vehicle.unavailable");
                }
            }

            // 4. Resolve optional equipments and compute equipment snapshot
            Set<Equipement> equipements = new HashSet<>();
            BigDecimal equipmentDailyTotal = BigDecimal.ZERO;
            if (equipementIds != null && !equipementIds.isEmpty()) {
                List<Equipement> list = equipementDao.findByIds(equipementIds);
                for (Equipement eq : list) {
                    equipements.add(eq);
                    if (eq.getPrixJournalier() != null) {
                        equipmentDailyTotal = equipmentDailyTotal.add(eq.getPrixJournalier());
                    }
                }
            }

            // 5. Build and save new reservation with snapshots (AD-04, AD-05)
            Utilisateur client = new Utilisateur();
            client.setId(clientId);

            Reservation r = new Reservation();
            r.setClient(client);
            r.setVehicule(v);
            r.setDateDebut(debut);
            r.setDateFin(fin);
            r.setDailyRateSnapshot(v.getTarifJournalier());
            r.setEquipmentDailyTotalSnapshot(equipmentDailyTotal);
            r.setEquipements(equipements);
            r.setStatut(StatutReservation.CONFIRMED);
            r.setCreatedAt(LocalDateTime.now(clock));

            reservationDao.save(r);
            return r;
        });
    }

    @Override
    public void annuler(Long actingUserId, Long reservationId) {
        tx.inTransaction(() -> {
            Reservation r = reservationDao.findById(reservationId);
            if (r == null) {
                throw new BusinessException("error.reservation.notfound");
            }
            if (r.getClient() != null && !Objects.equals(r.getClient().getId(), actingUserId)) {
                throw new BusinessException("error.access.denied");
            }
            if (r.getStatut() != StatutReservation.CONFIRMED) {
                throw new BusinessException("error.reservation.cannot_cancel");
            }
            r.setStatut(StatutReservation.CANCELLED);
            reservationDao.update(r);
        });
    }

    @Override
    public List<Reservation> historiqueClient(Long clientId) {
        return tx.inTransaction(() -> reservationDao.findByClient(clientId));
    }
}
