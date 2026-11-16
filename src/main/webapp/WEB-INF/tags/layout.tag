<%@ tag description="Main Application Layout" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${title != null ? title : 'Gestion Agence de Location'}"/> - CarRental</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        body { min-height: 100vh; display: flex; flex-direction: column; background-color: #f8f9fa; }
        main { flex: 1; }
        .navbar-brand { font-weight: 700; letter-spacing: 0.5px; }
        .badge-role-ADMIN { background-color: #dc3545; }
        .badge-role-AGENT { background-color: #0d6efd; }
        .badge-role-CLIENT { background-color: #198754; }
        .badge-status-AVAILABLE { background-color: #198754; }
        .badge-status-RENTED { background-color: #ffc107; color: #212529; }
        .badge-status-MAINTENANCE { background-color: #dc3545; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand" href="<c:url value='/home.action'/>">
            <i class="bi bi-car-front-fill me-2"></i>CarRental UIR
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <c:if test="${not empty sessionScope.user}">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/home.action'/>">
                            <i class="bi bi-house-door me-1"></i>Accueil
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/vehicules.action'/>">
                            <i class="bi bi-truck me-1"></i>Véhicules
                        </a>
                    </li>

                    <c:choose>
                        <c:when test="${sessionScope.user.role == 'ADMIN'}">
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/home.action'/>">
                                    <i class="bi bi-speedometer2 me-1"></i>Dashboard Admin
                                </a>
                            </li>
                        </c:when>
                        <c:when test="${sessionScope.user.role == 'AGENT'}">
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/home.action'/>">
                                    <i class="bi bi-key me-1"></i>Espace Agent
                                </a>
                            </li>
                        </c:when>
                        <c:when test="${sessionScope.user.role == 'CLIENT'}">
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/home.action'/>">
                                    <i class="bi bi-calendar-check me-1"></i>Mes Réservations
                                </a>
                            </li>
                        </c:when>
                    </c:choose>
                </c:if>
            </ul>

            <ul class="navbar-nav ms-auto align-items-center">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <li class="nav-item me-3 text-light">
                            <span class="badge badge-role-${sessionScope.user.role} me-1">
                                <c:out value="${sessionScope.user.role}"/>
                            </span>
                            <span class="fw-medium"><c:out value="${sessionScope.user.nomComplet}"/></span>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-outline-light btn-sm" href="<c:url value='/logout.action'/>">
                                <i class="bi bi-box-arrow-right me-1"></i>Déconnexion
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item me-2">
                            <a class="btn btn-outline-light btn-sm" href="<c:url value='/loginForm.action'/>">
                                <i class="bi bi-box-arrow-in-right me-1"></i>Connexion
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-primary btn-sm" href="<c:url value='/registerForm.action'/>">
                                <i class="bi bi-person-plus me-1"></i>Inscription
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<main class="container py-4">
    <!-- Messages and Alert notifications -->
    <s:if test="hasActionErrors()">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <s:actionerror/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </s:if>

    <s:if test="hasActionMessages()">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <s:actionmessage/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </s:if>

    <jsp:doBody/>
</main>

<footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
    <div class="container">
        <span>Car Rental Agency Management &bull; JEE Project UIR 2026/2027 &bull; Struts 2 &bull; Hibernate &bull; MySQL &bull; Tomcat 9</span>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
