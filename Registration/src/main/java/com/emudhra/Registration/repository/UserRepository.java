package com.emudhra.Registration.repository;
import com.emudhra.Registration.controller.AuthController;
import com.emudhra.Registration.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

public interface UserRepository extends JpaRepository<Users,Long> {
    @Autowired
    PasswordEncoder passwordEncoder = null;
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Users findByEmail(String email);

    @PostMapping("/google-login")
    default Map<String, String> googleLogin(@RequestBody Map<String, String> body, AuthController authController) {

        Map<String, String> response = new HashMap<>();

        try {
            String token = body.get("token");

            System.out.println("Received token: " + token);

            FirebaseToken decodedToken =
                    FirebaseAuth.getInstance().verifyIdToken(token);

            String email = decodedToken.getEmail();
            String preferredUsername = decodedToken.getName() != null
                    ? decodedToken.getName().replaceAll("\\s+", "")
                    : email.split("@")[0];
            System.out.println("Decoded Email: " + email);

            if (!email.endsWith("@kanchiuniv.ac.in")) {
                response.put("status", "FAIL");
                response.put("message", "Unauthorized domain");
                return response;
            }
            Users existingUser = findByEmail(email);

            if (existingUser == null) {
                String username = preferredUsername;
                int suffix = 1;
                while (existsByUsername(username)) {
                    username = preferredUsername + suffix;
                    suffix++;
                }

                String generatedPassword = authController.passwordEncoder.encode(decodedToken.getUid());
                Users user = new Users();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(generatedPassword);
                user.setRegistration_mode("GOOGLE_SSO");
                save(user);
            } else {
                existingUser.setRegistration_mode("GOOGLE_SSO");
                save(existingUser);
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
