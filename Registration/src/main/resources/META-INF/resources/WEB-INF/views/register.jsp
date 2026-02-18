<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Registration</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet" href="/css/style.css">

</head>

<body class="bg-light">

<c:if test="${not empty error}">
    <div class="position-fixed top-0 end-0 p-3" style="z-index: 1100;">
        <div id="errorToast"
             class="toast align-items-center text-white border-0"
             role="alert"
             aria-live="assertive"
             aria-atomic="true">

            <div class="d-flex">
                <div class="toast-body">
                        ${error}
                </div>
                <button type="button"
                        class="btn-close btn-close-white me-2 m-auto"
                        data-bs-dismiss="toast"
                        aria-label="Close">
                </button>
            </div>

        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            var toastEl = document.getElementById("errorToast");
            toastEl.style.backgroundColor = "rgba(255, 0, 0, 0.60)";
            var toast = new bootstrap.Toast(toastEl, { delay: 3000 });
            toast.show();
        });
    </script>
</c:if>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow p-4">

                <h3 class="text-center mb-4">Student Registration</h3>

                <!-- Manual Registration  -->
                <form id="registerForm" action="/register" method="post">

                    <div class="mb-3">
                        <label class="form-label">Username</label>
                        <input type="text"
                               id="username"
                               name="username"
                               class="form-control"
                               required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input type="email"
                               id="email"
                               name="email"
                               class="form-control"
                               pattern="^[A-Za-z0-9._%+-]+@kanchiuniv\.ac\.in$"
                               required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Password</label>
                        <input type="password"
                               id="password"
                               name="password"
                               class="form-control"
                               minlength="6"
                               maxlength="8"
                               required>
                    </div>

                    <button type="submit"
                            class="btn btn-primary w-100">
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
                    </form>
            </div>
        </div>
    </div>
</div>
</body>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://accounts.google.com/gsi/client" async defer></script>

<!-- Firebase Module -->
<script type="module">

    import { initializeApp } from
            "https://www.gstatic.com/firebasejs/12.9.0/firebase-app.js";

    import { getAuth, GoogleAuthProvider, signInWithPopup }
        from "https://www.gstatic.com/firebasejs/12.9.0/firebase-auth.js";

    import { signInWithRedirect, getRedirectResult }
        from "https://www.gstatic.com/firebasejs/12.9.0/firebase-auth.js";

    const firebaseConfig = {
        apiKey: "AIzaSyCGXRtrYvJG9-0m1R681_82cdIS1Atnv-o",
        authDomain: "registration-b5065.firebaseapp.com",
        projectId: "registration-b5065",
        storageBucket: "registration-b5065.firebasestorage.app",
        messagingSenderId: "545035780178",
        appId: "1:545035780178:web:93983fc187cdf4febedf8b",
        measurementId: "G-Z2ZF1P2YJC"
    };

    const app = initializeApp(firebaseConfig);
    const auth = getAuth(app);
    const provider = new GoogleAuthProvider();


    // Google Sign-In
    document.getElementById("googleSignIn")
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

                const response = await fetch("/google-login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ token: token })
                });

                const data = await response.json();

                if (data.status === "SUCCESS") {
                    window.location.href = "/login";
                } else {
                    alert(data.message);
                }

            } catch (error) {
                alert(error.message);
            }
        });

    getRedirectResult(auth)
        .then(async (result) => {

            if (!result) return;

            const user = result.user;
            const email = user.email;

            // Restrict to university domain
            if (!email.endsWith("@kanchiuniv.ac.in")) {
                alert("Only kanchiuniv.ac.in accounts allowed");
                return;
            }

            const token = await user.getIdToken();

            // Send token to Spring Boot backend
            const response = await fetch("/google-login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ token: token })
            });

            const data = await response.json();

            if (data.status === "SUCCESS") {
                window.location.href = "/dashboard";
            } else {
                alert(data.message);
            }

        })
        .catch(error => {
            console.error(error);
        });
</script>

