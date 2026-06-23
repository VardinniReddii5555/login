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
            <div class="col-md-7 col-lg-6">
                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">
                    <h3 class="text-center mb-3">JWT Authorization Grant Provider</h3>
                         <div class="toast-container position-fixed top-0 end-0 p-3">

                         </div>

                    <!-- Normal Login -->
                    <form action="${pageContext.request.contextPath}/jwt-authenticate" method="post" class="d-grid gap-3">
                        <input type="text" name="username" class="form-control" placeholder="Username" required>
                        <input type="password" name="password" class="form-control" placeholder="Password" required>
                        <button type="submit" class="btn btn-primary btn-md ">Login</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>









