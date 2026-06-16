<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MFA Setup</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>

<body>
<div class="auth-bg m-3">
    <jsp:include page="/WEB-INF/views/includes/error.jsp"/>
    <div class="container mt-2">
        <div class="row justify-content-center">
            <div class="col-md-9 col-lg-7">
                <div class="card shadow-sm border-0  text-center rounded-5 p-4 p-md-5">
                    <h3 class="text-center mb-3">Employee Login</h3>
                    <p class=" text-center text-muted">
                        Scan this QR code using Google Authenticator or  Microsoft Authenticator.
                     </p>
                    <div class="text-center mb-1">
                        <img src="data:image/png;base64,${qrCode}"
                             class="img-fluid border rounded-5 bg-white"
                             style="max-width:1000px;">
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger mt-3">
                            ${error}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/verify-registration"  method="post" class="d-grid gap-3>

                        <input type="hidden"
                               name="username"
                               value="${username}">
                        <div class="text-center mt-4 mb-2">    <label class="fw-semibold">  Enter Verification Code </label>    </div>
                        <div class="d-flex justify-content-center gap-2 mb-4">
                            <input type="text" maxlength="1" class="form-control otp-input text-center">
                            <input type="text" maxlength="1" class="form-control otp-input text-center">
                            <input type="text" maxlength="1" class="form-control otp-input text-center">
                            <input type="text" maxlength="1" class="form-control otp-input text-center">
                            <input type="text" maxlength="1" class="form-control otp-input text-center">
                            <input type="text" maxlength="1" class="form-control otp-input text-center">
                        </div>
                        <input type="hidden" id="otp" name="otp">
                        <button type="submit" class="btn btn-primary btn-md">   Verify & Activate MFA   </button>
                    </form>

                    <div class="text-center mt-4">
                        <a href="${pageContext.request.contextPath}/authenticator-login" class="text-decoration-none">
                            Already Registered? Login Here
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>

document.addEventListener("DOMContentLoaded", () => {

    const inputs =
        document.querySelectorAll(".otp-input");

    const hiddenOtp =
        document.getElementById("otp");

    inputs.forEach((input, index) => {

        input.addEventListener("input", (e) => {

            e.target.value =
                e.target.value.replace(/[^0-9]/g, "");

            if(e.target.value &&
               index < inputs.length - 1){

                inputs[index + 1].focus();
            }

            hiddenOtp.value =
                [...inputs]
                .map(i => i.value)
                .join("");
        });

        input.addEventListener("keydown", (e) => {

            if(e.key === "Backspace" &&
               !input.value &&
               index > 0){

                inputs[index - 1].focus();
            }
        });
    });
});

</script>

</body>
</html>
