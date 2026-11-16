<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Tableau de bord Administrateur">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1">Espace Administrateur</h2>
            <p class="text-muted">Supervision globale de l'agence de location</p>
        </div>
        <span class="badge bg-danger fs-6 px-3 py-2">Rôle : ADMINISTRATEUR</span>
    </div>

    <div class="row g-4 mb-4">
        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="p-3 bg-primary bg-opacity-10 text-primary rounded-3 me-3">
                            <i class="bi bi-car-front-fill fs-3"></i>
                        </div>
                        <div>
                            <h5 class="card-title fw-bold mb-0">Flotte Véhicules</h5>
                            <span class="text-muted small">Catalogue & Maintenance</span>
                        </div>
                    </div>
                    <p class="card-text text-muted small">Consultez l'ensemble du parc automobile, les statuts de disponibilité et les fiches véhicules.</p>
                    <a href="<c:url value='/vehicules.action'/>" class="btn btn-outline-primary btn-sm w-100">
                        <i class="bi bi-eye me-1"></i> Voir le catalogue
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="p-3 bg-success bg-opacity-10 text-success rounded-3 me-3">
                            <i class="bi bi-graph-up-arrow fs-3"></i>
                        </div>
                        <div>
                            <h5 class="card-title fw-bold mb-0">Revenus & Rapports</h5>
                            <span class="text-muted small">Indicateurs financiers</span>
                        </div>
                    </div>
                    <p class="card-text text-muted small">Chiffre d'affaires mensuel, ventilation par catégorie de véhicule et factures acquittées.</p>
                    <button class="btn btn-outline-success btn-sm w-100" disabled>
                        <i class="bi bi-bar-chart me-1"></i> Module reporting (Phase 6)
                    </button>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="p-3 bg-warning bg-opacity-10 text-warning rounded-3 me-3">
                            <i class="bi bi-cpu fs-3"></i>
                        </div>
                        <div>
                            <h5 class="card-title fw-bold mb-0">Modèle ML Risque</h5>
                            <span class="text-muted small">Intelligence Artificielle</span>
                        </div>
                    </div>
                    <p class="card-text text-muted small">Contrôle du modèle prédictif de risque d'annulation et de retour tardif. Réentraînement & métriques.</p>
                    <span class="badge bg-secondary mb-2">Statut : Modèle non entraîné</span>
                    <button class="btn btn-outline-warning btn-sm w-100 text-dark" disabled>
                        <i class="bi bi-gear me-1"></i> Contrôle ML (Phase 5)
                    </button>
                </div>
            </div>
        </div>
    </div>
</t:layout>
