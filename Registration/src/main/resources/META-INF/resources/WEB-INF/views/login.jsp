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

                    <hr/>

                    <!-- Google Login -->
                    <div class="text-center d-grid gap-2">

                        <button type="button"
                                id="firebaseSignIn"
                                class="btn btn-light border-secondary-subtle border-1 mb-3">

                            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                                 style="width:18px; margin-right:8px;">

                            Log in with Google(Firebase)
                        </button>

                         <a href="${pageContext.request.contextPath}/oauth2/authorization/google"
                                                   class="btn btn-outline-primary mb-3">
                                                    <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                                                                                    style="width:18px; margin-right:8px;">
                                                    Log in with Google (OAuth2)
                                                </a>
                        <a href="${pageContext.request.contextPath}/oauth2/authorization/emudhra"
                           class="btn btn-outline-purple mb-3 d-flex align-items-center justify-content-center">

                            <img src="https://tse4.mm.bing.net/th/id/OIP.tC5P0odIiwS-LEYoilTq8gHaHX?rs=1&pid=ImgDetMain"
                                 alt="Emudhra"
                                 style="width:20px; height:20px; margin-right:8px; object-fit:contain;">

                            Log in with Emudhra (OAuth2)
                        </a>
                        <a href="${pageContext.request.contextPath}/oauth2/authorization/github"
                           class="btn btn-outline-dark mb-3 d-flex align-items-center justify-content-center">

                            <img src="https://cdn-icons-png.flaticon.com/512/733/733553.png"
                                 alt="GitHub"
                                 style="width:20px; height:20px; margin-right:8px;">

                            Log in with GitHub (OAuth2)
                        </a>
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

<!-- Firebase Module -->
<script type="module">

    import { initializeApp }
        from "https://www.gstatic.com/firebasejs/12.9.0/firebase-app.js";

    import { getAuth, GoogleAuthProvider, signInWithPopup }
        from "https://www.gstatic.com/firebasejs/12.9.0/firebase-auth.js";

    const firebaseConfig = {
        apiKey: "AIzaSyCGXRtrYvJG9-0m1R681_82cdIS1Atnv-o",
        authDomain: "registration-b5065.firebaseapp.com",
        projectId: "registration-b5065",
        storageBucket: "registration-b5065.firebasestorage.app",
        messagingSenderId: "545035780178",
        appId: "1:545035780178:web:93983fc187cdf4febedf8b"
    };

    const app = initializeApp(firebaseConfig);
    const auth = getAuth(app);
    const provider = new GoogleAuthProvider();

    document.getElementById("firebaseSignIn")
        .addEventListener("click", async () => {

            try {

                const result = await signInWithPopup(auth, provider);
                const user = result.user;

                const email = user.email;

                // Restrict domain
                if (!email.endsWith("@kanchiuniv.ac.in")) {
                    alert("Only kanchiuniv.ac.in email allowed");
                    return;
                }

                const token = await user.getIdToken();

                const response = await fetch(
                    "${pageContext.request.contextPath}/google-login",
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({ token: token })
                    }
                );

                const data = await response.json();
                console.log("Backend response:", data);

                if (data.status === "SUCCESS") {
                    window.location.href =
                        "${pageContext.request.contextPath}/dashboard";
                } else {
                    alert(data.message);
                }

            } catch (error) {
                alert(error.message);
            }
        });

</script>

</body>
</html>
