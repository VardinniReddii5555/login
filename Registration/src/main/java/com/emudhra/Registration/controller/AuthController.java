package com.emudhra.Registration.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @PostMapping("/google-login")
    public Map<String, String> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("token");

        Map<String, String> response = new HashMap<>();

        try {
            String token = body.get("token");

            System.out.println("Received token: " + token);

            FirebaseToken decodedToken =
                    FirebaseAuth.getInstance().verifyIdToken(token);

            String email = decodedToken.getEmail();

            System.out.println("Decoded Email: " + email);

            if (!email.endsWith("@kanchiuniv.ac.in")) {
                response.put("status", "FAIL");
                response.put("message", "Unauthorized domain");
                return response;
            }

            response.put("status", "SUCCESS");
            response.put("message", "Login successful");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "FAIL");
            response.put("message", "Invalid token");
        }

        return response;
    }
}
