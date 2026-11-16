<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Catalogue des Véhicules">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1">Catalogue de la Flotte</h2>
            <p class="text-muted mb-0">Consultez l'ensemble des véhicules et leur statut en temps réel</p>
        </div>
        <div>
            <span class="badge bg-light text-dark border px-3 py-2">
                <i class="bi bi-car-front-fill me-1 text-primary"></i> Total : <c:out value="${vehicules != null ? vehicules.size() : 0}"/> véhicules
            </span>
        </div>
    </div>

    <!-- Status Legend -->
    <div class="card border-0 shadow-sm mb-4 bg-white">
        <div class="card-body py-2 d-flex flex-wrap align-items-center gap-3 small">
            <span class="fw-semibold text-secondary">Légende des statuts :</span>
            <span><span class="badge bg-success me-1"><i class="bi bi-check-circle me-1"></i>AVAILABLE</span> Disponible pour réservation</span>
            <span><span class="badge bg-warning text-dark me-1"><i class="bi bi-clock-history me-1"></i>RENTED</span> Actuellement en location</span>
            <span><span class="badge bg-danger me-1"><i class="bi bi-tools me-1"></i>MAINTENANCE</span> En révision technique</span>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty vehicules}">
            <div class="card border-0 shadow-sm p-5 text-center bg-white">
                <i class="bi bi-inbox text-muted" style="font-size: 3rem;"></i>
                <h5 class="mt-3 text-muted">Aucun véhicule enregistré</h5>
                <p class="text-muted small">Importez le fichier sql/dev-seed.sql dans MySQL pour peupler la flotte.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card border-0 shadow-sm bg-white">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light text-secondary text-uppercase small">
                            <tr>
                                <th scope="col" class="ps-3">Véhicule</th>
                                <th scope="col">Immatriculation</th>
                                <th scope="col">Catégorie</th>
                                <th scope="col">Caractéristiques</th>
                                <th scope="col">Kilométrage</th>
                                <th scope="col">Tarif Journalier</th>
                                <th scope="col" class="text-center">Statut</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="v" items="${vehicules}">
                                <tr>
                                    <td class="ps-3">
                                        <div class="d-flex align-items-center">
                                            <div class="p-2 bg-light rounded me-2">
                                                <i class="bi bi-car-front text-primary fs-5"></i>
                                            </div>
                                            <div>
                                                <div class="fw-bold"><c:out value="${v.marque}"/> <c:out value="${v.modele}"/></div>
                                                <span class="text-muted small"><c:out value="${v.places}"/> places</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-dark font-monospace border">
                                            <c:out value="${v.immatriculation}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge bg-info bg-opacity-10 text-info fw-semibold">
                                            <c:out value="${v.categorie.nom}"/>
                                        </span>
                                    </td>
                                    <td class="small text-muted">
                                        <div><i class="bi bi-fuel-pump me-1"></i><c:out value="${v.carburant}"/></div>
                                        <div><i class="bi bi-gear me-1"></i><c:out value="${v.boite}"/></div>
                                    </td>
                                    <td class="font-monospace small">
                                        <c:out value="${v.kilometrage}"/> km
                                    </td>
                                    <td class="fw-bold text-primary">
                                        <c:out value="${v.tarifJournalier}"/> DH <span class="text-muted fw-normal small">/jour</span>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${v.statut == 'AVAILABLE'}">
                                                <span class="badge bg-success px-3 py-2">
                                                    <i class="bi bi-check-circle me-1"></i>Disponible
                                                </span>
                                            </c:when>
                                            <c:when test="${v.statut == 'RENTED'}">
                                                <span class="badge bg-warning text-dark px-3 py-2">
                                                    <i class="bi bi-clock-history me-1"></i>En location
                                                </span>
                                            </c:when>
                                            <c:when test="${v.statut == 'MAINTENANCE'}">
                                                <span class="badge bg-danger px-3 py-2">
                                                    <i class="bi bi-tools me-1"></i>Maintenance
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary px-3 py-2">
                                                    <c:out value="${v.statut}"/>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</t:layout>
