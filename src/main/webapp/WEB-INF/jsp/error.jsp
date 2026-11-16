<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Erreur">
    <div class="row justify-content-center py-5">
        <div class="col-md-6 text-center">
            <div class="p-4 bg-white rounded-3 shadow-sm">
                <i class="bi bi-exclamation-triangle text-warning" style="font-size: 4rem;"></i>
                <h3 class="fw-bold mt-3">Une erreur est survenue</h3>
                <p class="text-muted">
                    L'opération demandée n'a pas pu aboutir ou vous ne disposez pas des autorisations nécessaires pour accéder à cette page.
                </p>
                <div class="mt-4">
                    <a href="<c:url value='/home.action'/>" class="btn btn-primary">
                        <i class="bi bi-house-door me-1"></i> Retour à l'accueil
                    </a>
                </div>
            </div>
        </div>
    </div>
</t:layout>
