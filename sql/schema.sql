-- Car Rental Agency Management - Database Schema (MySQL 8)
-- UTF-8 mb4 encoding with foreign keys, indexes, and integrity check constraints (T5, T11)

CREATE DATABASE IF NOT EXISTS car_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE car_rental;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS model_metrics;
DROP TABLE IF EXISTS facture;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS reservation_equipement;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS maintenance;
DROP TABLE IF EXISTS vehicule;
DROP TABLE IF EXISTS categorie;
DROP TABLE IF EXISTS equipement;
DROP TABLE IF EXISTS parametres;
DROP TABLE IF EXISTS utilisateur;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Utilisateur (Users & Roles)
CREATE TABLE utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prenom VARCHAR(80) NOT NULL,
    nom VARCHAR(80) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(72) NOT NULL,
    role VARCHAR(10) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_utilisateur_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Categorie (Vehicle Categories)
CREATE TABLE categorie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Vehicule (Fleet Vehicles)
CREATE TABLE vehicule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    marque VARCHAR(60) NOT NULL,
    modele VARCHAR(60) NOT NULL,
    immatriculation VARCHAR(20) NOT NULL UNIQUE,
    categorie_id BIGINT NOT NULL,
    carburant VARCHAR(15) NOT NULL,
    boite VARCHAR(12) NOT NULL,
    places INT NOT NULL,
    tarif_journalier DECIMAL(10,2) NOT NULL,
    kilometrage INT NOT NULL,
    photo VARCHAR(255),
    statut VARCHAR(15) NOT NULL DEFAULT 'AVAILABLE',
    INDEX idx_vehicule_status (statut),
    INDEX idx_vehicule_categorie (categorie_id),
    CONSTRAINT fk_vehicule_categorie FOREIGN KEY (categorie_id) REFERENCES categorie(id) ON DELETE RESTRICT,
    CONSTRAINT chk_vehicule_validity CHECK (places > 0 AND tarif_journalier > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Equipement (Rental Options)
CREATE TABLE equipement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    prix_journalier DECIMAL(8,2) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Reservation (Online Bookings)
-- Uses ON DELETE RESTRICT on client_id to prevent customer deletion from wiping revenue history
CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    vehicule_id BIGINT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    daily_rate_snapshot DECIMAL(10,2) NOT NULL,
    equipment_daily_total_snapshot DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    statut VARCHAR(15) NOT NULL DEFAULT 'CONFIRMED',
    created_at DATETIME NOT NULL,
    INDEX idx_reservation_vehicule_dates (vehicule_id, date_debut, date_fin),
    INDEX idx_reservation_client (client_id),
    INDEX idx_reservation_statut (statut),
    CONSTRAINT fk_reservation_client FOREIGN KEY (client_id) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_vehicule FOREIGN KEY (vehicule_id) REFERENCES vehicule(id) ON DELETE RESTRICT,
    CONSTRAINT chk_reservation_dates CHECK (date_fin >= date_debut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Reservation_Equipement (Many-to-Many Join Table)
CREATE TABLE reservation_equipement (
    reservation_id BIGINT NOT NULL,
    equipement_id BIGINT NOT NULL,
    PRIMARY KEY (reservation_id, equipement_id),
    CONSTRAINT fk_re_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE,
    CONSTRAINT fk_re_equipement FOREIGN KEY (equipement_id) REFERENCES equipement(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Location (Pick-up Contract)
-- Uses ON DELETE RESTRICT on reservation_id to preserve contractual contracts
CREATE TABLE location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE,
    agent_prise_id BIGINT NOT NULL,
    agent_retour_id BIGINT,
    date_prise DATE NOT NULL,
    date_retour_prevue DATE NOT NULL,
    date_retour_effective DATE,
    kilometrage_depart INT NOT NULL,
    kilometrage_retour INT,
    niveau_carburant_depart VARCHAR(20) NOT NULL,
    niveau_carburant_retour VARCHAR(20),
    created_at DATETIME NOT NULL,
    INDEX idx_location_reservation (reservation_id),
    INDEX idx_location_agent_prise (agent_prise_id),
    CONSTRAINT fk_location_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE RESTRICT,
    CONSTRAINT fk_location_agent_prise FOREIGN KEY (agent_prise_id) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    CONSTRAINT fk_location_agent_retour FOREIGN KEY (agent_retour_id) REFERENCES utilisateur(id) ON DELETE SET NULL,
    CONSTRAINT chk_location_kilometrage CHECK (kilometrage_retour IS NULL OR kilometrage_retour >= kilometrage_depart)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Facture (Invoices)
-- Uses ON DELETE RESTRICT on location_id to protect financial history
CREATE TABLE facture (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_id BIGINT NOT NULL UNIQUE,
    rental_days INT NOT NULL,
    late_days INT NOT NULL,
    base_amount DECIMAL(12,2) NOT NULL,
    equipment_amount DECIMAL(12,2) NOT NULL,
    late_fee_amount DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    vat_rate DECIMAL(6,4) NOT NULL,
    vat_amount DECIMAL(12,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    late_fee_rate DECIMAL(6,4) NOT NULL,
    emitted_at DATETIME NOT NULL,
    INDEX idx_facture_location (location_id),
    CONSTRAINT fk_facture_location FOREIGN KEY (location_id) REFERENCES location(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Maintenance (Fleet Service Records)
CREATE TABLE maintenance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicule_id BIGINT NOT NULL,
    date_operation DATE NOT NULL,
    type VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    cout DECIMAL(10,2) NOT NULL,
    kilometrage INT NOT NULL,
    INDEX idx_maintenance_vehicule (vehicule_id),
    CONSTRAINT fk_maintenance_vehicule FOREIGN KEY (vehicule_id) REFERENCES vehicule(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Parametres (Singleton Global Settings)
CREATE TABLE parametres (
    id BIGINT PRIMARY KEY,
    vat_rate DECIMAL(6,4) NOT NULL,
    late_fee_rate DECIMAL(6,4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Model_Metrics (ML Retraining & Performance History)
CREATE TABLE model_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trained_at DATETIME NOT NULL,
    model_type VARCHAR(80) NOT NULL,
    model_file VARCHAR(255),
    train_size INT NOT NULL,
    test_size INT NOT NULL,
    accuracy DECIMAL(6,4),
    precision_score DECIMAL(6,4),
    recall DECIMAL(6,4),
    f1_score DECIMAL(6,4),
    baseline_majority_accuracy DECIMAL(6,4),
    baseline_rule_f1 DECIMAL(6,4),
    notes VARCHAR(500),
    INDEX idx_model_metrics_trained_at (trained_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
