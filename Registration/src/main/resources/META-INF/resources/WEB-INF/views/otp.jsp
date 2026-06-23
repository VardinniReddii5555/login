<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/otp-style.css"/>
</head>

<body>
<div class="auth-bg d-flex justify-content-center align-items-center min-vh-100">
    <jsp:include page="/WEB-INF/views/includes/error.jsp"/>
    <div class="container mt-10">
        <div class="row justify-content-center">
            <div class="col-11 col-sm-10 col-md-8 col-lg-6 col-xl-5">
                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">
                    <h3 class="text-center mb-3">Email Verification</h3>
                    <p class="text-center text-muted">  Enter the OTP sent to your registered email. </p>
                    <c:if test="${param.otpError == 'true'}">
                        <div class="alert alert-danger" role="alert">
                            OTP entered incorrectly more than 2 times. Please login again.
                        </div>
                    </c:if>
                    <c:if test="${param.invalidOtp == 'true'}">
                        <div class="alert alert-warning" role="alert">
                            Invalid OTP. Please try again.
                        </div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/verify-otp" method="post" class="d-grid gap-3">
                        <form action="/verify-email-otp" method="post">

                                <div class="d-flex justify-content-center gap-2 mb-4">
                                    <input type="text" maxlength="1" class="form-control otp-input" required>
                                    <input type="text" maxlength="1" class="form-control otp-input" required>
                                    <input type="text" maxlength="1" class="form-control otp-input" required>
                                    <input type="text" maxlength="1" class="form-control otp-input" required>
                                    <input type="text" maxlength="1" class="form-control otp-input" required>
                                    <input type="text" maxlength="1" class="form-control otp-input" required>
                                </div>

                                <input type="hidden" id="otp" name="otp">
                            <button type="submit" class="btn btn-primary">Verify OTP</button>

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const inputs = document.querySelectorAll(".otp-input");
    const hiddenOtp = document.getElementById("otp");

    inputs.forEach((input, index) => {

        input.addEventListener("input", (e) => {
            e.target.value = e.target.value.replace(/[^0-9]/g, "");
            if (e.target.value && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
            hiddenOtp.value = [...inputs]
                .map(i => i.value)
                .join("");
        });

        input.addEventListener("keydown", (e) => {

            if (e.key === "Backspace" && !input.value && index > 0) {
                inputs[index - 1].focus();
            }
        });
    });
});
</script>
</body>
</html>