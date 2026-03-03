import { initializeApp } from "https://www.gstatic.com/firebasejs/12.9.0/firebase-app.js";
import {
    getAuth,
    GoogleAuthProvider,
    getRedirectResult,
    signInWithPopup
} from "https://www.gstatic.com/firebasejs/12.9.0/firebase-auth.js";

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
const ALLOWED_DOMAIN = "@kanchiuniv.ac.in";

function withContextPath(contextPath, path) {
    return `${contextPath}${path}`;
}

function ensureAllowedDomain(email, message) {
    if (!email.endsWith(ALLOWED_DOMAIN)) {
        alert(message);
        return false;
    }

    return true;
}

async function exchangeTokenAndRedirect({ user, contextPath, successRedirectPath }) {
    const token = await user.getIdToken();
    const response = await fetch(withContextPath(contextPath, "/google-login"), {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ token })
    });

    const data = await response.json();

    if (data.status === "SUCCESS") {
        window.location.href = withContextPath(contextPath, successRedirectPath);
        return;
    }

    alert(data.message);
}

async function processRedirectResult({ contextPath, redirectSuccessPath }) {
    try {
        const result = await getRedirectResult(auth);

        if (!result) {
            return;
        }

        const user = result.user;
        const email = user.email || "";

        if (!ensureAllowedDomain(email, "Only kanchiuniv.ac.in accounts allowed")) {
            return;
        }

        await exchangeTokenAndRedirect({
            user,
            contextPath,
            successRedirectPath: redirectSuccessPath
        });
    } catch (error) {
        console.error(error);
    }
}

export function attachGoogleSignIn({
    buttonId,
    contextPath = "",
    successRedirectPath,
    enableRedirectResult = false,
    redirectSuccessPath = "/dashboard"
}) {
    const button = document.getElementById(buttonId);

    if (button) {
        button.addEventListener("click", async () => {
            try {
                const result = await signInWithPopup(auth, provider);
                const user = result.user;
                const email = user.email || "";

                if (!ensureAllowedDomain(email, "Only kanchiuniv.ac.in email allowed")) {
                    return;
                }

                await exchangeTokenAndRedirect({
                    user,
                    contextPath,
                    successRedirectPath
                });
            } catch (error) {
                alert(error.message);
            }
        });
    }

    if (enableRedirectResult) {
        processRedirectResult({
            contextPath,
            redirectSuccessPath
        });
    }
}