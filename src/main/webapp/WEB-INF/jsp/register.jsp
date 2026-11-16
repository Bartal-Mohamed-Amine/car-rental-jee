<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Inscription Client">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-plus text-primary" style="font-size: 2.5rem;"></i>
                        <h4 class="mt-2 fw-bold">Créer un compte client</h4>
                        <p class="text-muted small">Réservez vos véhicules en ligne en quelques clics</p>
                    </div>

                    <s:form action="register" method="post" theme="simple">
                        <s:token/>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Prénom</label>
                                <s:textfield name="prenom" cssClass="form-control" placeholder="Jean"/>
                                <s:fielderror fieldName="prenom" cssClass="text-danger small mt-1"/>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Nom</label>
                                <s:textfield name="nom" cssClass="form-control" placeholder="Dupont"/>
                                <s:fielderror fieldName="nom" cssClass="text-danger small mt-1"/>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Adresse Email</label>
                            <s:textfield name="email" cssClass="form-control" placeholder="jean.dupont@email.com"/>
                            <s:fielderror fieldName="email" cssClass="text-danger small mt-1"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Mot de passe</label>
                            <s:password name="plainPassword" cssClass="form-control" placeholder="Au moins 6 caractères"/>
                            <s:fielderror fieldName="plainPassword" cssClass="text-danger small mt-1"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Confirmer le mot de passe</label>
                            <s:password name="confirmPassword" cssClass="form-control" placeholder="Retapez le mot de passe"/>
                            <s:fielderror fieldName="confirmPassword" cssClass="text-danger small mt-1"/>
                        </div>

                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-primary py-2 fw-semibold">
                                <i class="bi bi-check-circle me-1"></i> Créer mon compte
                            </button>
                        </div>
                    </s:form>

                    <hr class="my-4"/>

                    <div class="text-center">
                        <span class="text-muted small">Déjà inscrit ?</span>
                        <a href="<c:url value='/loginForm.action'/>" class="small fw-semibold ms-1">Connectez-vous</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
