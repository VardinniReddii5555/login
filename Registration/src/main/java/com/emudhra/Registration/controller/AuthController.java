package com.emudhra.Registration.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import com.emudhra.Registration.service.AuthResponseFactory;
import com.emudhra.Registration.service.AuthResult;
import com.emudhra.Registration.service.GoogleLoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final GoogleLoginService googleLoginService;
    private final AuthResponseFactory authResponseFactory;

    public AuthController(GoogleLoginService googleLoginService, AuthResponseFactory authResponseFactory) {
        this.googleLoginService = googleLoginService;
        this.authResponseFactory = authResponseFactory;
    }


    @PostMapping("/google-login")
    public Map<String, String> googleLogin(@RequestBody Map<String, String> body, HttpSession session) {
        String idToken = body.get("token");
        AuthResult result = googleLoginService.loginWithGoogleToken(idToken, session);
        if (result.success()) {
            return authResponseFactory.success(result.message());
        }

        return authResponseFactory.failure(result.message());
    }
}