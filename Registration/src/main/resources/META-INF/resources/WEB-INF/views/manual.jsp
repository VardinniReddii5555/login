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
<div class="auth-bg">

    <!-- Error Toast -->
    <c:if test="${not empty error}">
        <div class="position-fixed top-0 end-0 p-3" style="z-index:1100;">
            <div class="toast align-items-center text-white border-0 bg-danger show">
                <div class="d-flex">
                    <div class="toast-body">${error}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto"
                            data-bs-dismiss="toast"></button>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Success Toast -->
    <c:if test="${not empty message}">
        <div class="position-fixed top-0 end-0 p-3" style="z-index:1100; margin-top:70px;">
            <div class="toast align-items-center text-white border-0 bg-success show">
                <div class="d-flex">
                    <div class="toast-body">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto"
                            data-bs-dismiss="toast"></button>
                </div>
            </div>
        </div>
    </c:if>

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-5">
                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">

                    <h1 class="fw-semibold mb-4">Student Login</h1>
                    </c:if>
                    <!-- Normal Login -->
                    <form action="${pageContext.request.contextPath}/login"
                          method="post"
                          class="d-grid gap-3">

                        <input type="text" name="username" class="form-control " placeholder="Username" required>
                        <input type="password" name="password" class="form-control" placeholder="Password" required>
                        <button type="submit"
                                class="btn btn-primary btn-md mt-2">
                            Login
                        </button>
                    </form>

                    <p class="text-center text-secondary mt-4 mb-0">
                        New user?
                        <a href="${pageContext.request.contextPath}/register"
                           class="fw-semibold">Register</a>
                    </p>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
