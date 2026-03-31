package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.service.SocialLoginService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FirebaseLoginController {

    private final SocialLoginService socialLoginService;
    private final LoginAuditService loginAuditService;

    public FirebaseLoginController(SocialLoginService socialLoginService,
                                   LoginAuditService loginAuditService) {
        this.socialLoginService = socialLoginService;
        this.loginAuditService = loginAuditService;
    }

    @PostMapping("/google-login")
    public Map<String, String> googleLogin(@RequestBody Map<String, String> body, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(body.get("token"));
            String email = decodedToken.getEmail();
            if (email == null || !email.endsWith("@kanchiuniv.ac.in")) {
                response.put("status", "FAIL");
                response.put("message", "Unauthorized domain");
                return response;
            }

            Users activeUser = socialLoginService.findOrCreateUser(
                    email,
                    decodedToken.getName(),
                    decodedToken.getUid(),
                    LoginModes.FIREBASE_SSO);

            loginAuditService.startSessionAudit(activeUser, session, LoginModes.FIREBASE_SSO);
            session.setAttribute("user", activeUser);
            response.put("status", "SUCCESS");
            response.put("message", "Login successful");
        } catch (Exception e) {
            response.put("status", "FAIL");
            response.put("message", "Google Authentication Failed");
        }
        return response;
    }
}