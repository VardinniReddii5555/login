<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Registration</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet" href="/css/style.css">

</head>

<body class="auth-bg">
    <%@ include file="/WEB-INF/views/common/toast.jspf" %>
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-5">

                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">

                    <h3 class="text-center mb-4">Student Registration</h3>

                    <!-- Manual Registration  -->
                    <form id="registerForm" action="/register" method="post" class="d-grip gap-3">

                         <label class="form-label">Username</label>
                            <input type="text" id="username" name="username" class="form-control " required>

                         <label class="form-label">Email</label>
                            <input type="email"  id="email" name="email" class="form-control " pattern="^[A-Za-z0-9._%+-]+@kanchiuniv\.ac\.in$" required>

                         <label class="form-label">Password</label>
                            <input type="password" id="password" name="password" class="form-control "  minlength="6" maxlength="8" required>

                        <button type="submit"
                                class="btn btn-primary btn-md col-12 mt-3 d-block mx-auto">
                            Register Manually
                        </button>

                        <hr/>

                        <div class="text-center">
                            <button type="button"
                                    id="googleSignIn"
                                    class="btn btn-#300 border-#800 border-3 mb-3">

                                <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                                     alt="Google logo"
                                     style="width:18px; height:18px; margin-right:8px;">

                                Sign in with Google
                            </button>
                        </div>

                        <p class="text-center text-secondary mt-1 mb-0">
                            Already Registered?
                            <a href="${pageContext.request.contextPath}/login"
                               class="fw-semibold">Login</a>
                        </p>
                        </form>
                </div>
            </div>
        </div>
    </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <!-- Firebase Module -->
        <script type="module">
        import { attachGoogleSignIn } from "${pageContext.request.contextPath}/common/google-auth.js";

                attachGoogleSignIn({
                    buttonId: "googleSignIn",
                    contextPath: "${pageContext.request.contextPath}",
                    successRedirectPath: "/login",
                    enableRedirectResult: true,
                    redirectSuccessPath: "/dashboard"
                });
        </script>
    </body>
</html>
