<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration Successful</title>

    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #e3f2fd, #ffffff);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .success-card {
            max-width: 500px;
            border-radius: 15px;
        }
    </style>
</head>
<body>

<div class="card shadow-lg text-center p-5 success-card">
    <div class="mb-4">
        <i class="bi bi-check-circle-fill text-success" style="font-size: 4rem;"></i>
    </div>

    <h2 class="text-success fw-bold">Registration Successful!</h2>

    <p class="mt-3 text-muted">
        Your account has been created successfully.
        You can now proceed to login and explore the system.
    </p>

    <div class="mt-4">
        <a href="/register" class="btn btn-primary px-4">
            Another Registration
        </a>
        <a href="/" class="btn btn-outline-secondary px-4 ms-2">
            Home
        </a>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
