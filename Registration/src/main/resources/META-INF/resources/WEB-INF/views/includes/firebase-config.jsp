<script type="module">
    import { initializeApp } from "https://www.gstatic.com/firebasejs/12.9.0/firebase-app.js";
    import { getAuth, GoogleAuthProvider, signInWithPopup } from "https://www.gstatic.com/firebasejs/12.9.0/firebase-auth.js";

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

    document.getElementById("firebaseSignIn").addEventListener("click", async () => {
        try {
            const result = await signInWithPopup(auth, provider);
            const user = result.user;
            if (!user.email.endsWith("@kanchiuniv.ac.in")) {
                alert("Only @kanchiuniv.ac.in email allowed");
                return;
            }

            const response = await fetch("${pageContext.request.contextPath}/google-login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: await user.getIdToken() })
            });

            const data = await response.json();
            if (data.status === "SUCCESS") {
                window.location.href = "${pageContext.request.contextPath}/dashboard";
                return;
            }
            alert(data.message);
        } catch (error) {
            alert(error.message);
        }
    });
</script>