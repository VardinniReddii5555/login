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
                        <input type="text" name="otp" maxlength="6" placeholder="Enter OTP" class="form-control" required>
                            <button type="submit" class="btn btn-primary">Verify OTP</button>

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>