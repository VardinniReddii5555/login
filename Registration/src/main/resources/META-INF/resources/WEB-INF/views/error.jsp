<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error Page</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">
<div class="container d-flex justify-content-center align-items-center vh-100">
    <div class="card shadow-lg p-5 text-center" style="max-width: 500px;">

        <h1 class="text-danger fw-bold">⚠ Error</h1>

        <p class="mt-3 text-muted">
            Something went wrong. Please try again.
        </p>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger mt-3">
                ${errorMessage}
            </div>
        </c:if>

        <c:if test="${not empty status}">
            <p class="mt-2">Status Code: ${status}</p>
        </c:if>

        <div class="mt-4">
            <a href="/login" class="btn btn-primary">Go to Login</a>
            <a href="/" class="btn btn-secondary">Home</a>
        </div>

    </div>
</div>

</body>
</html>