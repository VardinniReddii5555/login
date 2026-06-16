<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">


<title>MFA Registration</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet">

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/style.css"/>


</head>

<body>

<div class="auth-bg d-flex justify-content-center align-items-center min-vh-100">


<div class="container mt-10">

    <div class="row justify-content-center">

        <div class="col-11 col-sm-10 col-md-8 col-lg-6 col-xl-5">

            <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">

                <div class="text-center mb-4">

                    <div class="mb-3">

                        <span style="font-size: 60px;">
                            🔐
                        </span>

                    </div>

                    <h2 class="fw-bold">
                        MFA Registration
                    </h2>

                    <p class="text-muted mb-0">
                        Secure your account using
                        Google Authenticator
                    </p>

                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        ${error}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/mfa-register"
                      method="post">

                    <div class="mb-4">

                        <label class="form-label fw-semibold">

                            Username

                        </label>

                        <input type="text"
                               name="username"
                               class="form-control form-control-lg"
                               placeholder="Enter your username"
                               required>

                    </div>

                    <button type="submit"
                            class="btn btn-primary btn-lg w-100">

                        Generate QR Code

                    </button>

                </form>

                <hr class="my-4">

                <div class="text-center">

                    <p class="text-muted mb-2"> Already registered? </p>
                    <a href="${pageContext.request.contextPath}/authenticator-login"
                       class="btn btn-outline-success"> Login Using Authenticator </a>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</body>
</html>
