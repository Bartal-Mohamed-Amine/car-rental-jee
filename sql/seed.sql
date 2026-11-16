-- Car Rental Agency Management - Development Seed Data
-- 3 demo accounts (BCrypt hashed with factor 12), categories, equipment, 10 vehicles,
-- active rentals for RENTED vehicles (including 1 overdue rental), and maintenance record for MAINTENANCE vehicle.

USE car_rental;

-- 1. Singleton Global Settings
INSERT INTO parametres (id, vat_rate, late_fee_rate)
VALUES (1, 0.2000, 1.5000)
ON DUPLICATE KEY UPDATE vat_rate = 0.2000, late_fee_rate = 1.5000;

-- 2. Demo Users for Each Role
-- Passwords:
--   admin@rental.ma   -> admin123
--   agent@rental.ma   -> agent123
--   client@rental.ma  -> client123
INSERT INTO utilisateur (id, prenom, nom, email, password_hash, role, actif) VALUES
(1, 'Karim', 'Alami', 'admin@rental.ma', '$2a$12$oug1HjEmTWqckfp0k4zcC.9gvHEzJKuTYUPpDCAyXdcaYvzZtT0Jy', 'ADMIN', 1),
(2, 'Fatima', 'Benjelloun', 'agent@rental.ma', '$2a$12$uX/KU/1NeupI7xS5Mb2XA.nr2Cn3Dpb1CnqucbWtA0yZcDgZoS1M6', 'AGENT', 1),
(3, 'Yassine', 'Bennani', 'client@rental.ma', '$2a$12$InsNJFc8aFFpho3I0ATOLu53P51xX/kFe1bjiAVUdGzX6auz9hGNa', 'CLIENT', 1)
ON DUPLICATE KEY UPDATE prenom=VALUES(prenom);

-- 3. Vehicle Categories
INSERT INTO categorie (id, nom, description) VALUES
(1, 'Économique', 'Véhicules urbains compacts à faible consommation'),
(2, 'Compacte', 'Polyvalentes confortables pour ville et route'),
(3, 'Berline', 'Routières spacieuses et confort haut de gamme'),
(4, 'SUV', 'Véhicules surélevés et spacieux pour familles et voyages'),
(5, 'Luxe & Prestige', 'Véhicules haut de gamme grand confort')
ON DUPLICATE KEY UPDATE nom=VALUES(nom);

-- 4. Optional Equipment
INSERT INTO equipement (id, nom, description, prix_journalier, actif) VALUES
(1, 'GPS Navigation Pro', 'Système de guidage par satellite avec alertes trafic en direct', 40.00, 1),
(2, 'Siège Enfant / Bébé', 'Siège auto homologué Isofix pour enfants de 9 mois à 4 ans', 35.00, 1),
(3, 'Conducteur Additionnel', 'Assurance et autorisation de conduite pour un 2ème conducteur', 50.00, 1),
(4, 'Chaînes à Neige', 'Kit de chaînes haute adhérence pour parcours montagne', 30.00, 1)
ON DUPLICATE KEY UPDATE nom=VALUES(nom);

-- 5. 10 Fleet Vehicles (7 AVAILABLE, 2 RENTED, 1 MAINTENANCE)
INSERT INTO vehicule (id, marque, modele, immatriculation, categorie_id, carburant, boite, places, tarif_journalier, kilometrage, photo, statut) VALUES
(1, 'Dacia', 'Logan', '12345-A-1', 1, 'DIESEL', 'MANUELLE', 5, 250.00, 45200, NULL, 'AVAILABLE'),
(2, 'Renault', 'Clio 5', '23456-B-2', 1, 'ESSENCE', 'MANUELLE', 5, 280.00, 31400, NULL, 'AVAILABLE'),
(3, 'Peugeot', '208', '34567-D-6', 2, 'DIESEL', 'MANUELLE', 5, 320.00, 22100, NULL, 'AVAILABLE'),
(4, 'Volkswagen', 'Golf 8', '45678-A-10', 2, 'DIESEL', 'AUTOMATIQUE', 5, 450.00, 18500, NULL, 'RENTED'),
(5, 'Hyundai', 'Tucson', '56789-C-15', 4, 'HYBRIDE', 'AUTOMATIQUE', 5, 600.00, 27800, NULL, 'AVAILABLE'),
(6, 'Toyota', 'RAV4', '67890-E-20', 4, 'HYBRIDE', 'AUTOMATIQUE', 5, 650.00, 35000, NULL, 'RENTED'),
(7, 'Peugeot', '508', '78901-A-30', 3, 'DIESEL', 'AUTOMATIQUE', 5, 550.00, 41200, NULL, 'AVAILABLE'),
(8, 'Mercedes-Benz', 'Classe C 220d', '89012-B-50', 5, 'DIESEL', 'AUTOMATIQUE', 5, 950.00, 15300, NULL, 'AVAILABLE'),
(9, 'BMW', 'Série 3', '90123-C-60', 5, 'ESSENCE', 'AUTOMATIQUE', 5, 980.00, 12000, NULL, 'AVAILABLE'),
(10, 'Dacia', 'Duster', '11223-D-70', 4, 'DIESEL', 'MANUELLE', 5, 380.00, 58400, NULL, 'MAINTENANCE')
ON DUPLICATE KEY UPDATE marque=VALUES(marque);

-- 6. Active Reservations for Rented Vehicles
INSERT INTO reservation (id, client_id, vehicule_id, date_debut, date_fin, daily_rate_snapshot, equipment_daily_total_snapshot, statut, created_at) VALUES
(1, 3, 4, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 3 DAY, 450.00, 0.00, 'PICKED_UP', NOW() - INTERVAL 10 DAY),
(2, 3, 6, CURDATE() - INTERVAL 6 DAY, CURDATE() - INTERVAL 1 DAY, 650.00, 40.00, 'PICKED_UP', NOW() - INTERVAL 20 DAY)
ON DUPLICATE KEY UPDATE statut=VALUES(statut);

-- Equipment for Reservation 2 (GPS)
INSERT INTO reservation_equipement (reservation_id, equipement_id) VALUES
(2, 1)
ON DUPLICATE KEY UPDATE equipement_id=VALUES(equipement_id);

-- 7. Active Locations (Contracts) for Rented Vehicles
-- Note: Location 2 date_retour_prevue is yesterday (CURDATE() - INTERVAL 1 DAY), creating a 1-day overdue rental for late demo
INSERT INTO location (id, reservation_id, agent_prise_id, date_prise, date_retour_prevue, kilometrage_depart, niveau_carburant_depart, created_at) VALUES
(1, 1, 2, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 3 DAY, 18400, 'FULL', NOW()),
(2, 2, 2, CURDATE() - INTERVAL 6 DAY, CURDATE() - INTERVAL 1 DAY, 34800, 'FULL', NOW())
ON DUPLICATE KEY UPDATE kilometrage_depart=VALUES(kilometrage_depart);

-- 8. Maintenance record for Vehicle 10
INSERT INTO maintenance (id, vehicule_id, date_operation, type, description, cout, kilometrage) VALUES
(1, 10, CURDATE() - INTERVAL 1 DAY, 'Vidange et freins', 'Révision 60 000 km', 1800.00, 58400)
ON DUPLICATE KEY UPDATE type=VALUES(type);
