<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Registration</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="auth-bg">
<jsp:include page="/WEB-INF/views/includes/error.jsp"/>
<div class="container m-5">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">

            <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">

                <h3 class="text-center mt-2 mb-5">Employee Registration Portal</h3>

                <!-- Manual Registration  -->
                <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post" class="d-grid gap-3">

                    <input type="text" name="username" id="username"  class="form-control" placeholder="Create Your Username"required>
                    <input type="email" name="email" id="email"  class="form-control"  placeholder="Enter Your Mail-ID" pattern="^[A-Za-z0-9._%+-]+@^[A-Za-z0-9._%+-]$" required>
                    <input type="password" name="password" id="password"  class="form-control"  placeholder="Create Your Password" minlength="6" maxlength="8" required>
                    <button type="submit" class="btn btn-primary btn-md mt-4">Register Manually</button>

                    <hr/>
                    <p class="text-center text-secondary mt-1 mb-0">Already Registered?
                        <a href="${pageContext.request.contextPath}/login" class="fw-semibold">Login</a>
                    </p>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>