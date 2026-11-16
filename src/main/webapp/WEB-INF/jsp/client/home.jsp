<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Espace Client">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1">Bienvenue sur votre espace client</h2>
            <p class="text-muted">Réservez votre véhicule idéal pour vos déplacements</p>
        </div>
        <span class="badge bg-success fs-6 px-3 py-2">Rôle : CLIENT</span>
    </div>

    <div class="row g-4 mb-4">
        <div class="col-md-6">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="p-3 bg-success bg-opacity-10 text-success rounded-3 me-3">
                            <i class="bi bi-search fs-3"></i>
                        </div>
                        <div>
                            <h5 class="card-title fw-bold mb-0">Catalogue des Véhicules</h5>
                            <span class="text-muted small">Citadines, Berlines, SUV & Hybrides</span>
                        </div>
                    </div>
                    <p class="card-text text-muted small">Explorez notre parc automobile, visualisez les tarifs journaliers et vérifiez la disponibilité.</p>
                    <a href="<c:url value='/vehicules.action'/>" class="btn btn-success btn-sm">
                        <i class="bi bi-car-front me-1"></i> Parcourir les véhicules
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="p-3 bg-primary bg-opacity-10 text-primary rounded-3 me-3">
                            <i class="bi bi-receipt fs-3"></i>
                        </div>
                        <div>
                            <h5 class="card-title fw-bold mb-0">Mes Réservations & Factures</h5>
                            <span class="text-muted small">Historique de vos locations</span>
                        </div>
                    </div>
                    <p class="card-text text-muted small">Consultez vos réservations à venir, téléchargez vos factures détaillées avec TVA et suppléments.</p>
                    <button class="btn btn-outline-secondary btn-sm" disabled>
                        <i class="bi bi-calendar-event me-1"></i> Module réservations (Phase 2 & 3)
                    </button>
                </div>
            </div>
        </div>
    </div>
</t:layout>
