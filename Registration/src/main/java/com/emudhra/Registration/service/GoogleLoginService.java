package com.emudhra.Registration.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.HttpSession;

@Service
public class GoogleLoginService {

    private static final String ALLOWED_DOMAIN = "@kanchiuniv.ac.in";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAuditService loginAuditService;

    public GoogleLoginService(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              LoginAuditService loginAuditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAuditService = loginAuditService;
    }

    public AuthResult loginWithGoogleToken(String idToken, HttpSession session) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();

            if (email == null || !email.endsWith(ALLOWED_DOMAIN)) {
                return AuthResult.failure("Unauthorized domain");
            }

            Users activeUser = upsertGoogleUser(decodedToken, email);
            session.setAttribute("user", activeUser);
            loginAuditService.startSessionAudit(activeUser, session, LoginModes.GOOGLE_SSO);

            return AuthResult.success("Login successful");
        } catch (Exception exception) {
            exception.printStackTrace();
            return AuthResult.failure("Google Authentication Failed");
        }
    }

    private Users upsertGoogleUser(FirebaseToken decodedToken, String email) {
        Users existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            existingUser.setRegistration_mode(LoginModes.GOOGLE_SSO);
            return userRepository.save(existingUser);
        }

        String preferredUsername = decodedToken.getName() != null
                ? decodedToken.getName().replaceAll("\\s+", "")
                : email.split("@")[0];

        if (preferredUsername.length() > 50) {
            preferredUsername = preferredUsername.substring(0, 50);
        }

        String uniqueUsername = buildUniqueUsername(preferredUsername);

        Users user = new Users();
        user.setUsername(uniqueUsername);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(decodedToken.getUid()));
        user.setRegistration_mode(LoginModes.GOOGLE_SSO);

        return userRepository.save(user);
    }

    private String buildUniqueUsername(String preferredUsername) {
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

        return username;
    }
}