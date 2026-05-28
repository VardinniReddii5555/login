<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manual Login</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>

<body>
<div class="auth-bg">
    <jsp:include page="/WEB-INF/views/includes/error.jsp"/>
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-5">
                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">

                    <h1 class="fw-semibold mb-4">Student Login</h1>
                    <!-- Normal Login -->
                        <form action="${pageContext.request.contextPath}/manual" method="post" class="d-grid gap-3">
                        <input type="text" name="username" class="form-control" placeholder="Username" required>
                        <input type="password" name="password" class="form-control" placeholder="Password" required>
                        <button type="submit" class="btn btn-primary btn-md mt-2">Login</button>
                        </form>

                         <p class="text-center text-secondary mt-4 mb-0">New user?
                         <a href="${pageContext.request.contextPath}/register" class="fw-semibold">Register</a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
