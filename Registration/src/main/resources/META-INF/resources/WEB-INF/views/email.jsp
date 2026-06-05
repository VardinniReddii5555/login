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
                    <p class="text-center text-muted">  Enter your registered email. </p>
                    <c:if test="${param.otpExpired == 'true'}">
                        <div class="alert alert-danger" role="alert">
                            OTP has expired. Please request a new OTP.
                        </div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/email"  method="post" class="d-grid gap-3">
                        <input type="email" name="email" placeholder"Enter registered Email" class="form-control" required>
                        <button type="submit"class=" justify-content-center btn btn-primary">Send OTP</button>
                    </form>

                     <p class="text-center text-secondary mt-3 mb-0">
                                            New user?
                                            <a href="${pageContext.request.contextPath}/register"
                                               class="fw-semibold">Register</a>
                                        </p>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>