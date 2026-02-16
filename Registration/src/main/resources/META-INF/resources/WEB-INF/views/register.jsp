<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Registration</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow p-4">

                <h3 class="text-center mb-4">Student Registration</h3>

                <!-- Google Sign-In Button -->
                <button type="button"
                        id="googleSignIn"
                        class="btn btn-danger w-100 mb-3">
                    Sign in with Google
                </button>

                <hr/>

                <!-- Manual Registration (Optional) -->
                <form id="registerForm">

                    <div class="mb-3">
                        <label class="form-label">Username</label>
                        <input type="text"
                               id="username"
                               class="form-control">
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input type="email"
                               id="email"
                               class="form-control">
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Password</label>
                        <input type="password"
                               id="password"
                               class="form-control">
                    </div>

                    <button type="submit"
                            class="btn btn-primary w-100">
                        Register Manually
                    </button>

                </form>

            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

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
                    window.location.href = "/success";
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
                window.location.href = "/success.jsp";
            } else {
                alert(data.message);
            }

        })
        .catch(error => {
            console.error(error);
        });



    // Manual Registration (Optional)
    document.getElementById("registerForm")
        .addEventListener("submit", async function (e) {

            e.preventDefault();

            const username = document.getElementById("username").value.trim();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();

            if (!username || !email || !password) {
                alert("All fields required");
                return;
            }

            if (!email.endsWith("@kanchiuniv.ac.in")) {
                alert("Email must end with @kanchiuniv.ac.in");
                return;
            }

            if (password.length < 6 || password.length > 8) {
                alert("Password must be 6-8 characters");
                return;
            }

            const response = await fetch("/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, email, password })
            });

            const data = await response.json();

            if (data.status === "SUCCESS") {
                window.location.href = "/success";
            } else {
                alert(data.message);
            }
        });

</script>

</body>
</html>
