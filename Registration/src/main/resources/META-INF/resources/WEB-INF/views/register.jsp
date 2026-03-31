<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Registration</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet" href="/css/style.css">

</head>

<body class="auth-bg">
<c:set var="toastMessage" value="" />
<c:set var="toastType" value="" />

<c:choose>
    <c:when test="${not empty error and not empty fn:trim(error)}">
        <c:set var="toastMessage" value="${error}" />
        <c:set var="toastType" value="error" />
    </c:when>
    <c:when test="${not empty success and not empty fn:trim(success)}">
        <c:set var="toastMessage" value="${success}" />
        <c:set var="toastType" value="success" />
    </c:when>
</c:choose>

<c:if test="${not empty toastMessage}">
    <div class="position-fixed top-0 end-0 p-3" style="z-index: 1100;">
        <div id="errorToast"
             class="toast align-items-center text-white border-0"
             role="alert"
             aria-live="assertive"
             aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                        ${toastMessage}
                </div>
                <button type="button"
                        class="btn-close btn-close-white me-2 m-auto"
                        data-bs-dismiss="toast"
                        aria-label="Close">
                </button>
            </div>

        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            var toastEl = document.getElementById("errorToast");
            if (!toastEl) {
                            return;
                        }

                        var toastType = "${toastType}";
                        toastEl.style.backgroundColor = toastType === "success"
                            ? "rgba(25, 135, 84, 0.30)"
                            : "rgba(255, 0, 0, 0.30)";

            var toast = new bootstrap.Toast(toastEl, { delay: 3000 });
            toast.show();
        });
    </script>
</c:if>

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-5">

            <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">

                <h3 class="text-center mb-4">Student Registration</h3>

                <!-- Manual Registration  -->
                <form id="registerForm" action="/register" method="post" class="d-grip gap-3">

                     <label class="form-label">Username</label>
                        <input type="text" id="username" name="username" class="form-control " required>

                     <label class="form-label">Email</label>
                        <input type="email"  id="email" name="email" class="form-control " pattern="^[A-Za-z0-9._%+-]+@kanchiuniv\.ac\.in$" required>

                     <label class="form-label">Password</label>
                        <input type="password" id="password" name="password" class="form-control "  minlength="6" maxlength="8" required>

                    <button type="submit"
                            class="btn btn-primary btn-md col-12 mt-3 d-block mx-auto">
                        Register Manually
                    </button>

                    <hr/>
                    <p class="text-center text-secondary mt-1 mb-0">
                        Already Registered?
                        <a href="${pageContext.request.contextPath}/login"
                           class="fw-semibold">Login</a>
                    </p>
                    </form>
            </div>
        </div>
    </div>
</div>
</body>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://accounts.google.com/gsi/client" async defer></script>
