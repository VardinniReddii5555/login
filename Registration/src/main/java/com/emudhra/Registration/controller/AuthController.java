package com.emudhra.Registration.controller;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/google-login")
    public Map<String, String> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("token");

        Map<String, String> response = new HashMap<>();

        try {
            String token = body.get("token");

            System.out.println("Received token: " + token);

            FirebaseToken decodedToken =
                    FirebaseAuth.getInstance().verifyIdToken(idToken);

            String email = decodedToken.getEmail();
            String preferredUsername = decodedToken.getName() != null
                    ? decodedToken.getName().replaceAll("\\s+", "")
                    : email.split("@")[0];

            if (preferredUsername.length() > 50) {
                preferredUsername = preferredUsername.substring(0, 50);
            }

            System.out.println("Decoded Email: " + email);

            if (!email.endsWith("@kanchiuniv.ac.in")) {
                response.put("status", "FAIL");
                response.put("message", "Unauthorized domain");
                return response;
            }

            Users existingUser = userRepository.findByEmail(email);

            if (existingUser == null) {
                String username = preferredUsername;
                int suffix = 1;
                while (userRepository.existsByUsername(username)) {
                    String suffixValue = String.valueOf(suffix);
                    int maxBaseLength = 50 - suffixValue.length();
                    String base = preferredUsername.length() > maxBaseLength
                            ? preferredUsername.substring(0, maxBaseLength)
                            : preferredUsername;
                    username = base + suffixValue;
                    suffix++;
                }

                String generatedPassword = passwordEncoder.encode(decodedToken.getUid());
                Users user = new Users();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(generatedPassword);
                user.setRegistration_mode("GOOGLE_SSO");
                userRepository.save(user);
            } else {
                existingUser.setRegistration_mode("GOOGLE_SSO");
                userRepository.save(existingUser);
            }

            response.put("status", "SUCCESS");
            response.put("message", "Login successful");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "FAIL");
            response.put("message", "Google Authentication Failed");
        }

        return response;
    }
}