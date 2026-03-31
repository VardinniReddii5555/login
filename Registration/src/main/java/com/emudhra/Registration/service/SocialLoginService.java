package com.emudhra.Registration.service;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SocialLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SocialLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users findOrCreateUser(String email,
                                  String preferredName,
                                  String providerUserId,
                                  String loginMode) {
        Users existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            existingUser.setRegistration_mode(loginMode);
            return userRepository.save(existingUser);
        }

        String preferredUsername = normalizePreferredUsername(preferredName, email);
        String username = makeUniqueUsername(preferredUsername);
        String generatedPassword = passwordEncoder.encode(providerUserId == null ? email : providerUserId);

        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(generatedPassword);
        newUser.setRegistration_mode(loginMode);
        return userRepository.save(newUser);
    }

    private String normalizePreferredUsername(String preferredName, String email) {
        String candidate = (preferredName != null && !preferredName.isBlank())
                ? preferredName.replaceAll("\\s+", "")
                : email.split("@")[0];
        return candidate.length() <= 50 ? candidate : candidate.substring(0, 50);
    }

    private String makeUniqueUsername(String preferredUsername) {
        String username = preferredUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            String suffixValue = String.valueOf(suffix++);
            int maxBaseLength = 50 - suffixValue.length();
            String base = preferredUsername.length() > maxBaseLength
                    ? preferredUsername.substring(0, maxBaseLength)
                    : preferredUsername;
            username = base + suffixValue;
        }
        return username;
    }
}