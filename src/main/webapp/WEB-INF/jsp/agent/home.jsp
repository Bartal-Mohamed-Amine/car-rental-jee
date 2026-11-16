<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Espace Agent">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1">Espace Agent de Comptoir</h2>
            <p class="text-muted">Gestion opérationnelle des départs, retours et de la flotte</p>
        </div>
        <span class="badge bg-primary fs-6 px-3 py-2">Rôle : AGENT</span>
    </div>

    <div class="row g-4 mb-4">
        <div class="col-md-6">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="p-3 bg-primary bg-opacity-10 text-primary rounded-3 me-3">
                            <i class="bi bi-car-front fs-3"></i>
                        </div>
                        <div>
                            <h5 class="card-title fw-bold mb-0">Parc Automobile</h5>
                            <span class="text-muted small">Disponibilité en temps réel</span>
                        </div>
                    </div>
                    <p class="card-text text-muted small">Consultez l'état de chaque véhicule de la flotte, identifiez les véhicules disponibles, en location ou en révision.</p>
                    <a href="<c:url value='/vehicules.action'/>" class="btn btn-primary btn-sm">
                        <i class="bi bi-list-ul me-1"></i> Consulter la flotte
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="p-3 bg-info bg-opacity-10 text-info rounded-3 me-3">
                            <i class="bi bi-arrow-left-right fs-3"></i>
                        </div>
                        <div>
                            <h5 class="card-title fw-bold mb-0">Prises & Retours</h5>
                            <span class="text-muted small">Cycle de location</span>
                        </div>
                    </div>
                    <p class="card-text text-muted small">Enregistrement des contrats de départ (kilométrage, carburant, score de risque ML) et clôture avec facturation automatique.</p>
                    <button class="btn btn-outline-secondary btn-sm" disabled>
                        <i class="bi bi-clock-history me-1"></i> Module locations (Phase 2 & 3)
                    </button>
                </div>
            </div>
        </div>
    </div>
</t:layout>
