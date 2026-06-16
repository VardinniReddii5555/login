<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Authenticator Login</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"  rel="stylesheet">
<link rel="stylesheet"  href="${pageContext.request.contextPath}/css/style.css"/>
</head>

<body>
<div class="auth-bg d-flex justify-content-center align-items-center min-vh-100">
    <div class="container mt-10">
        <div class="row justify-content-center">
            <div class="col-11 col-sm-10 col-md-8 col-lg-6 col-xl-5">
                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">
                    <h3 class="text-center mb-3"> Authenticator Login </h3>

                    <p class="text-center text-muted">  Enter your username and the 6-digit code from Google Authenticator. </p>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">
                            ${error}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/authenticator-login"
                          method="post">

                        <div class="mb-4">
                            <input type="text" name="username" class="form-control" placeholder="Enter Username" required>  </div>

                        <div class="text-center mb-2">
                            <label class="form-label">    6 Digit Authenticator Code    </label>
                        </div>

                        <div class="d-flex justify-content-center gap-2 mb-4">

                            <input type="text" maxlength="1" class="form-control otp-input" required>
                            <input type="text" maxlength="1" class="form-control otp-input" required>
                            <input type="text" maxlength="1" class="form-control otp-input" required>
                            <input type="text" maxlength="1" class="form-control otp-input" required>
                            <input type="text" maxlength="1" class="form-control otp-input" required>
                            <input type="text" maxlength="1" class="form-control otp-input" required>

                        </div>

                        <input type="hidden"  id="otp" name="otp">
                        <button type="submit" class="btn btn-primary w-100">  Verify & Login </button>
                    </form>
                    <div class="text-center mt-4">
                        <span class="text-muted">   New User?    </span>
                        <a href="${pageContext.request.contextPath}/mfa-register"> Register MFA </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>

document.addEventListener("DOMContentLoaded", () => {

    const inputs = document.querySelectorAll(".otp-input");
    const hiddenOtp = document.getElementById("otp");

    inputs.forEach((input, index) => {

        input.addEventListener("input", (e) => {
            e.target.value =   e.target.value.replace(/[^0-9]/g, "");
            if (e.target.value && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }

            hiddenOtp.value =
                [...inputs]
                .map(i => i.value)
                .join("");
        });
        input.addEventListener("keydown", (e) => {

            if (e.key === "Backspace"
                && !input.value
                && index > 0) {

                inputs[index - 1].focus();
            }
        });
    });
});
</script>
</body>
</html>

