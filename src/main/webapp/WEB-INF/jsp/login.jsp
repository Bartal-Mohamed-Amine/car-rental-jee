<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Connexion">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-shield-lock text-primary" style="font-size: 2.5rem;"></i>
                        <h4 class="mt-2 fw-bold">Connexion</h4>
                        <p class="text-muted small">Accédez à votre espace CarRental</p>
                    </div>

                    <s:form action="login" method="post" theme="simple">
                        <s:token/>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Adresse Email</label>
                            <s:textfield name="email" cssClass="form-control" placeholder="nom@exemple.com"/>
                            <s:fielderror fieldName="email" cssClass="text-danger small mt-1"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Mot de passe</label>
                            <s:password name="plainPassword" cssClass="form-control" placeholder="••••••••"/>
                            <s:fielderror fieldName="plainPassword" cssClass="text-danger small mt-1"/>
                        </div>

                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-primary py-2 fw-semibold">
                                <i class="bi bi-box-arrow-in-right me-1"></i> Se connecter
                            </button>
                        </div>
                    </s:form>

                    <hr class="my-4"/>

                    <div class="text-center">
                        <span class="text-muted small">Vous n'avez pas de compte ?</span>
                        <a href="<c:url value='/registerForm.action'/>" class="small fw-semibold ms-1">Inscrivez-vous ici</a>
                    </div>
                </div>
            </div>

            <!-- Demo accounts reminder for evaluation -->
            <div class="card mt-3 bg-light border-0">
                <div class="card-body p-3">
                    <h6 class="card-title fw-bold text-secondary mb-2 small text-uppercase">Comptes de démonstration :</h6>
                    <ul class="list-unstyled mb-0 small text-muted">
                        <li><strong>Admin:</strong> admin@rental.ma / admin123</li>
                        <li><strong>Agent:</strong> agent@rental.ma / agent123</li>
                        <li><strong>Client:</strong> client@rental.ma / client123</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</t:layout>
