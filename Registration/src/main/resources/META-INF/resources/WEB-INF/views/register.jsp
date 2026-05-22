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
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-5">

                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">

                    <h3 class="text-center mb-4">Student Registration</h3>

                    <!-- Manual Registration  -->
                     <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post" class="d-grid gap-3">
                        <label class="form-label">Username</label>
                                        <input type="text" id="username" name="username" class="form-control" required>
                        <label class="form-label">Email</label>
                                        <input type="email" id="email" name="email" class="form-control" pattern="^[A-Za-z0-9._%+-]+@^[A-Za-z0-9._%+-]$" required>
                        <label class="form-label">Password</label>
                                        <input type="password" id="password" name="password" class="form-control" minlength="6" maxlength="8" required>
                        <button type="submit" class="btn btn-primary btn-md col-12 mt-3 d-block mx-auto">Register Manually</button>

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