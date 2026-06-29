<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Device Verification</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow border-0 rounded-4 p-5 text-center" style="max-width: 450px; width: 100%;">

        <div class="spinner-border text-success" role="status" style="width: 3.5rem; height: 3.5rem; margin-bottom: 25px;">
            <span class="visually-hidden">Loading...</span>
        </div>

        <h3 class="fw-bold mb-2">Check your device</h3>
        <p class="text-secondary mb-4">
            A verification prompt has been pushed to the device registered for
            <strong class="text-dark">${username}</strong>.
        </p>

        <div class="alert alert-info py-2" role="alert" style="font-size: 14px;">
            Please open your device and tap <strong>"Yes, it's me"</strong> to approve this session.
        </div>

        <hr class="my-4">

        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none text-muted small">
            Cancel and return to login
        </a>
    </div>
</div>

<script>
    // Automatically execute the background workflow once the page finishes loading
    document.addEventListener("DOMContentLoaded", function () {
        const usernameVal = "${username}";

        // 1. Send the POST request to start the tracking lifecycle
        fetch('${pageContext.request.contextPath}/api/push-auth/initiate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'username=' + encodeURIComponent(usernameVal)
        })
            .then(res => res.json())
            .then(data => {
                if (data.requestId) {
                    // 2. Begin listening loops for user device click choices
                    startStatusShortPolling(data.requestId);
                } else {
                    alert("Could not process authentication tracking session parameters.");
                }
            })
            .catch(err => {
                console.error("Initiation fault:", err);
                alert("Error connecting to the validation service network.");
            });
    });

    function startStatusShortPolling(requestId) {
        const tracker = setInterval(() => {
            fetch('${pageContext.request.contextPath}/api/push-auth/check-status/' + requestId)
                .then(res => res.json())
                .then(data => {
                    if (data.status === "APPROVED") {
                        clearInterval(tracker);
                        window.location.href = "${pageContext.request.contextPath}/dashboard";
                    }
                    else if (data.status === "DENIED") {
                        clearInterval(tracker);
                        alert("Sign-in authorization was explicitly denied on your handset.");
                        window.location.href = "${pageContext.request.contextPath}/login";
                    }
                    else if (data.status === "EXPIRED") {
                        clearInterval(tracker);
                        alert("The authorization request session window expired.");
                        window.location.href = "${pageContext.request.contextPath}/login";
                    }
                })
                .catch(err => console.error("Communication channel tracing error:", err));
        }, 2000); // Verify device interaction maps every 2 seconds cleanly
    }
</script>

</body>
</html>