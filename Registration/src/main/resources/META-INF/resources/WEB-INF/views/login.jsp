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
<div class="auth-bg m-3">
    <jsp:include page="/WEB-INF/views/includes/error.jsp"/>
    <div class="container mt-2">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-6">
                <div class="card shadow-sm border-0 rounded-4 p-4 p-md-5">
                    <h3 class="text-center mb-3">Employee Login</h3>
                         <div class="toast-container position-fixed top-0 end-0 p-3">

                             <c:if test="${param.oauth2Error == 'true'}">
                                 <div id="oauthToast"
                                      class="toast text-bg-danger border-0"
                                      role="alert">

                                     <div class="d-flex">
                                         <div class="toast-body">
                                             OAuth2 login failed or unauthorized domain.
                                         </div>
                                         <button type="button"
                                                 class="btn-close btn-close-white me-2 m-auto"
                                                 data-bs-dismiss="toast">
                                         </button>
                                     </div>

                                 </div>
                             </c:if>

                             <c:if test="${param.emudhraStrictError == 'true'}">
                                 <div id="emudhraToast"
                                      class="toast text-bg-danger border-0"
                                      role="alert">

                                     <div class="d-flex">
                                         <div class="toast-body">
                                             Strict Emudhra SSO validation failed:
                                             missing/invalid ID token or access token.
                                         </div>
                                         <button type="button"
                                                 class="btn-close btn-close-white me-2 m-auto"
                                                 data-bs-dismiss="toast">
                                         </button>
                                     </div>

                                 </div>
                             </c:if>

                             <c:if test="${param.invalidOtp == 'true'}">
                                 <div id="invalidOtpToast"
                                      class="toast text-bg-warning border-0"
                                      role="alert">

                                     <div class="d-flex">
                                         <div class="toast-body">
                                             Invalid OTP. Please try again.
                                         </div>
                                         <button type="button"
                                                 class="btn-close me-2 m-auto"
                                                 data-bs-dismiss="toast">
                                         </button>
                                     </div>

                                 </div>
                             </c:if>

                             <c:if test="${param.otpError == 'true'}">
                                 <div id="otpErrorToast"
                                      class="toast text-bg-danger border-0"
                                      role="alert">

                                     <div class="d-flex">
                                         <div class="toast-body">
                                             OTP entered incorrectly more than 2 times.
                                             Please login again.
                                         </div>
                                         <button type="button"
                                                 class="btn-close btn-close-white me-2 m-auto"
                                                 data-bs-dismiss="toast">
                                         </button>
                                     </div>

                                 </div>
                             </c:if>

                         </div>

                    <!-- Normal Login -->
                    <form action="${pageContext.request.contextPath}/login" method="post" class="d-grid gap-3">
                        <input type="text" name="username" class="form-control" placeholder="Username" required>
                        <input type="password" name="password" class="form-control" placeholder="Password" required>
                        <button type="submit" class="btn btn-primary btn-md ">Login</button>
                        <a href="${pageContext.request.contextPath}/email"class="btn btn-outline-primary btn-md">
                                                    Login with Email Authentication </a>
                    </form>
                    <hr/>

                    <!-- Auth Login -->
                     <div class="text-center d-grid gap-1">
                        <button type="button" id="firebaseSignIn" class="btn btn-light border-secondary-subtle border-1 mb-3">
                                 <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" class="logo">
                                       Log in with Google(Firebase) </button>

                        <a href="${pageContext.request.contextPath}/oauth2/authorization/google" class="btn btn-outline-primary mb-3">
                            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" class="logo">Log in with Google (OAuth2)</a>
                        <a href="${pageContext.request.contextPath}/oauth2/authorization/github" class="btn btn-outline-dark mb-3">
                            <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" class="logo">Log in with GitHub (OAuth2)</a>

                        <a href="${pageContext.request.contextPath}/oauth2/authorization/emudhra" class="btn btn-outline-warning mb-3">
                            <img src="https://yt3.googleusercontent.com/t2BW9FUl35Nc7u7wES6eHYeS4mQ5-ic8kOjEkh-YYY-YLTMjm9hgJ53MTjkeXCBBX5SbJvtQzFI=s900-c-k-c0x00ffffff-no-rj" class="logo">Log in with Emudhra [OIDC] (OAuth2)</a>
                        <a href="${pageContext.request.contextPath}/saml2/authenticate/emudhra" class="btn btn-outline-purple mb-3">
                            <img src="https://yt3.googleusercontent.com/t2BW9FUl35Nc7u7wES6eHYeS4mQ5-ic8kOjEkh-YYY-YLTMjm9hgJ53MTjkeXCBBX5SbJvtQzFI=s900-c-k-c0x00ffffff-no-rj" class="logo">Log in with Emudhra [SAMl] (OAuth2)</a>

                        <a href="${pageContext.request.contextPath}/oauth2/authorization/employee-portal" class="btn btn-outline-dark mb-3">
                            <img src="https://thf.bing.com/th/id/ODF.jc9sIfRjWdw7BWr4dkaj7g?w=32&h=32&qlt=90&pcl=fffffc&o=6&pid=1.2" class="logo">Log in with Keyclock [OIDC]</a>
                        <a href="${pageContext.request.contextPath}/saml2/authenticate/keycloak" class="btn btn-outline-dark mb-3">
                            <img src="https://thf.bing.com/th/id/ODF.jc9sIfRjWdw7BWr4dkaj7g?w=32&h=32&qlt=90&pcl=fffffc&o=6&pid=1.2" class="logo">Log in with Keyclock [SAML]</a>

                    </div>

                    <p class="text-center text-secondary mt-2 mb-0">
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
<script>
document.addEventListener("DOMContentLoaded", function () {

    document.querySelectorAll(".toast").forEach(function (toastEl) {

        const toast = new bootstrap.Toast(toastEl, {
            delay: 5000,
            autohide: true
        });

        toast.show();
    });

});
</script>
<jsp:include page="/WEB-INF/views/includes/firebase-config.jsp"/>
</body>
</html>