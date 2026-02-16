<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

</head>

<body class="bg-light">

<div class="container mt-5">

    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow p-4">

                <h3 class="text-center mb-4">User Registration</h3>

                <form id="registerForm">

                    <!-- Username -->
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text"
                               id="username"
                               name="username"
                               class="form-control">
                    </div>

                    <!-- Email -->
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email"
                               id="email"
                               name="email"
                               class="form-control">
                    </div>

                    <!-- Password -->
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password"
                               id="password"
                               name="password"
                               class="form-control">
                    </div>

                    <button type="submit"
                            class="btn btn-primary w-100">
                        Register
                    </button>

                </form>

            </div>

        </div>
    </div>

</div>

<!-- Toast -->
<div class="toast-container position-fixed top-0 end-0 p-3">

    <div id="errorToast"
         class="toast align-items-center text-bg-danger border-0"
         role="alert">

        <div class="d-flex">
            <div class="toast-body" id="toastMsg">
                Message
            </div>

            <button type="button"
                    class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast">
            </button>
        </div>

    </div>

</div>

<!-- Bootstrap JS (Place at bottom for proper loading) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>

    document.addEventListener("DOMContentLoaded", function () {

        const form = document.getElementById("registerForm");

        form.addEventListener("submit", function(e) {

            e.preventDefault();

            const username = document.getElementById("username").value.trim();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();

            if (!username || !email || !password) {
                showToast("All fields are required!");
                return;
            }

            if (!email.endsWith("@gmail.com")) {
                showToast("Email must end with @gmail.com");
                return;
            }

            if (password.length < 6 || password.length > 8) {
                showToast("Password must be 6 to 8 characters");
                return;
            }

            const formData = new FormData(form);

            fetch("/register", {
                method: "POST",
                body: formData
            })
                .then(response => response.json())
                .then(data => {

                    let toastElement = document.getElementById("errorToast");

                    // Remove previous color classes
                    toastElement.classList.remove("bg-danger", "bg-success");

                    if (data.status === "SUCCESS") {
                        toastElement.classList.add("bg-success");
                    } else {
                        toastElement.classList.add("bg-danger");
                    }

                    document.getElementById("toastMsg").innerText = data.message;

                    let toast = new bootstrap.Toast(toastElement);
                    toast.show();

                    if (data.status === "SUCCESS") {
                        document.getElementById("registerForm").reset();
                        window.location.href = "/success";   // if needed
                    }

                })

        });

    });

    function showToast(message) {
        document.getElementById("toastMsg").innerText = message;
        const toastElement = document.getElementById("errorToast");
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
    }

</script>

</body>
</html>
