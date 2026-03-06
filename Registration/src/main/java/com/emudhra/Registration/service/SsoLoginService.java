package com.emudhra.Registration.service;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class SsoLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAuditService loginAuditService;

    public SsoLoginService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           LoginAuditService loginAuditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAuditService = loginAuditService;
    }

    public AuthResult loginWithOidc(OidcUser oidcUser, HttpSession session, String loginMode) {
        String email = oidcUser.getEmail();
        if (email == null || email.isBlank()) {
            return AuthResult.failure("Email claim is missing from identity provider response");
        }

        String subject = oidcUser.getSubject();
        if (subject == null || subject.isBlank()) {
            subject = email;
        }

        Users activeUser = upsertSsoUser(email, oidcUser.getFullName(), subject, loginMode);
        session.setAttribute("user", activeUser);
        loginAuditService.startSessionAudit(activeUser, session, loginMode);

        return AuthResult.success("Login successful");
    }

    private Users upsertSsoUser(String email, String displayName, String uniqueSourceId, String loginMode) {
        Users existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            existingUser.setRegistration_mode(loginMode);
            return userRepository.save(existingUser);
        }

        String preferredUsername = (displayName != null && !displayName.isBlank())
                ? displayName.replaceAll("\\s+", "")
                : email.split("@")[0];

        if (preferredUsername.length() > 50) {
            preferredUsername = preferredUsername.substring(0, 50);
        }

        String uniqueUsername = buildUniqueUsername(preferredUsername);

        Users user = new Users();
        user.setUsername(uniqueUsername);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(uniqueSourceId));
        user.setRegistration_mode(loginMode);

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