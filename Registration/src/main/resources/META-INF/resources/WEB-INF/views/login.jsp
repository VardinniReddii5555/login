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
    <jsp:include page="/WEB-INF/views/includes/error.jsp"/>
    <div class="container py-1">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-5">
                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">
                    <h1 class="fw-semibold mt-2 mb-3">Student Login</h1>
                         <c:if test="${param.oauth2Error == 'true'}">
                            <div class="alert alert-danger" role="alert">
                                OAuth2 login failed or unauthorized domain.
                            </div>
                         </c:if>

                    <!-- Normal Login -->
                    <form action="${pageContext.request.contextPath}/login" method="post" class="d-grid gap-3">
                        <input type="text" name="username" class="form-control" placeholder="Username" required>
                        <input type="password" name="password" class="form-control" placeholder="Password" required>
                        <button type="submit" class="btn btn-primary btn-md mt-2">Login</button>
                    </form>

                    <hr/>

                    <!-- Auth Login -->
                    <div class="text-center d-grid gap-2">

                        <button type="button" id="firebaseSignIn" class="btn btn-light border-secondary-subtle border-1 mb-3">
                                                     <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" style="width:18px; margin-right:8px;">
                                                     Log in with Google(Firebase)
                                                 </button>

                        <a href="${pageContext.request.contextPath}/oauth2/authorization/google" class="btn btn-outline-primary mb-3">Log in with Google (OAuth2)</a>
                        <a href="${pageContext.request.contextPath}/oauth2/authorization/emudhra" class="btn btn-outline-purple mb-3">Log in with Emudhra (OAuth2)</a>
                        <a href="${pageContext.request.contextPath}/oauth2/authorization/github" class="btn btn-outline-dark mb-3">Log in with GitHub (OAuth2)</a>
                    </div>

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
<script ><jsp:include page="/WEB-INF/views/includes/firebase-config.jsp"/></script>
</body>
</html>