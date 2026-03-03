<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body class="auth-bg">
<nav class="navbar bg-white shadow-sm">
    <div class="container">
        <span class="navbar-brand fw-semibold">HEYLLO!!! ${sessionScope.user.username}</span>
        <form action="/logout" method="post" style="display:inline;">

            <button type="submit"
                    style="background-color: rgba(255, 0, 0, 0.8); color: white;">
                LOG OUT
            </button>
        </form>

    </div>
</nav>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card border-0 shadow-sm rounded-4 p-4">
                <h2 class="fw-bold mb-4">User Details</h2>
                <dl class="row mb-0">
                    <dt class="col-sm-4">ID</dt>
                    <dd class="col-sm-8">${sessionScope.user.id}</dd>

                    <dt class="col-sm-4">Username</dt>
                    <dd class="col-sm-8">${sessionScope.user.username}</dd>

                    <dt class="col-sm-4">Email</dt>
                    <dd class="col-sm-8">${sessionScope.user.email}</dd>

                    <dt class="col-sm-4">Mode</dt>
                    <dd class="col-sm-8">${sessionScope.user.registration_mode}</dd>
                </dl>
            </div>
        </div>
    </div>
</div>
</body>
</html>