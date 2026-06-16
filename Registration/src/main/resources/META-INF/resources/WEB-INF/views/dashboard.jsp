<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | SECUREPass Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-style.css"/>
</head>

<body>
<nav class="navbar navbar-expand-lg navbar-custom shadow-sm">
    <div class="container">

        <a class="navbar-brand fw-bold mb-0" href="#">
            <i class="fa-solid fa-shield-halved me-2"></i>
            Employee Portal
        </a>

        <div class="dropdown">

            <button class="profile-btn"
                    type="button"
                    data-bs-toggle="dropdown"
                    aria-expanded="false">

                <i class="fa-solid fa-circle-user fs-4"></i>

                <span>${username}</span>

                <i class="fa-solid fa-chevron-down small"></i>

            </button>

            <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 rounded-4 mt-2">

                <li class="px-3 py-2">
                    <div class="text-muted small">
                        Signed in as
                    </div>
                    <div class="fw-semibold">
                        ${username}
                    </div>
                </li>

                <li>
                    <hr class="dropdown-divider">
                </li>

                <li>
                    <form action="/logout"
                          method="post"
                          class="m-0">

                        <button type="submit"
                                class="dropdown-item logout-item">

                            <i class="fa-solid fa-right-from-bracket me-2"></i>

                            Logout

                        </button>

                    </form>
                </li>

            </ul>

        </div>

    </div>
</nav>
<div class="container py-4">

    <!-- Hero Banner -->

    <div class="card hero-card shadow-lg rounded-4 mb-4">
        <div class="card-body p-5">
            <div class="row align-items-center">
                <div class="col-md-8">
                   <!-- <span class="badge bg-light text-primary badge-modern mb-3">
                         Dashboard
                    </span>--!>
                    <h1 class="fw-bold">
                        Welcome back,
                        ${username}
                    </h1>
                    <p class="mb-0 opacity-75">
                        Access your profile, account settings, and organizational resources.
                    </p>
                </div>
                <div class="col-md-4 text-center">
                    <i class="fa-solid fa-user-shield"
                       style="font-size:110px; opacity:.25;"></i>
                </div>
            </div>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="row g-4 mb-4">
        <div class="col-md-6 col-lg-3">
            <div class="card stat-card shadow-sm h-100">
                <div class="card-body text-center p-4">
                    <div class="icon-circle bg-soft-primary">
                        <i class="fa-solid fa-id-card"></i>
                    </div>
                    <p class="text-muted mt-3 mb-1">
                        User ID
                    </p>
                    <h4 class="fw-bold">
                        ${id}
                    </h4>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card stat-card shadow-sm h-100">
                <div class="card-body text-center p-4">
                    <div class="icon-circle bg-soft-success">
                        <i class="fa-solid fa-user"></i>
                    </div>
                    <p class="text-muted mt-3 mb-1">
                        Username
                    </p>
                    <h4 class="fw-bold">
                        ${username}
                    </h4>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card stat-card shadow-sm h-100">
                <div class="card-body text-center p-4">
                    <div class="icon-circle bg-soft-info">
                        <i class="fa-solid fa-envelope"></i>
                    </div>
                    <p class="text-muted mt-3 mb-1">
                        Email
                    </p>
                    <div class="fw-semibold small">
                        ${email}
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card stat-card shadow-sm h-100">
                <div class="card-body text-center p-4">
                    <div class="icon-circle bg-soft-warning">
                        <i class="fa-solid fa-lock"></i>
                    </div>
                    <p class="text-muted mt-3 mb-1">
                        Login Mode
                    </p>
                    <h5 class="fw-bold">
                        ${loginMode}
                    </h5>
                </div>
            </div>
        </div>
    </div>

    <!-- User Details -->

    <div class="card details-card shadow-sm">
        <div class="card-header bg-white border-0 pt-4">
            <h4 class="section-title">
                <i class="fa-solid fa-address-card text-primary me-2"></i>
                User Details
            </h4>
        </div>

        <div class="card-body">
            <div class="row g-4">
                <div class="col-md-6">
                    <div class="detail-box">
                        <div class="text-muted small mb-2">
                            User ID
                        </div>
                        <div class="fw-bold fs-5">
                            ${id}
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="detail-box">
                        <div class="text-muted small mb-2">
                            Username
                        </div>
                        <div class="fw-bold fs-5">
                            ${username}
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="detail-box">
                        <div class="text-muted small mb-2">
                            Email Address
                        </div>
                        <div class="fw-semibold">
                            ${email}
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="detail-box">
                        <div class="text-muted small mb-2">
                            Registration Mode
                        </div>
                        <span class="badge bg-primary badge-modern">
                            ${registration_mode}
                        </span>
                    </div>
                </div>
                <div class="col-md-12">
                    <div class="detail-box">
                        <div class="text-muted small mb-2">
                            Login Mode
                        </div>
                        <span class="badge bg-success badge-modern">
                            ${loginMode}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

