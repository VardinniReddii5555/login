package com.emudhra.Registration.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;

@Service
public class RegistrationService {

    private static final String ALLOWED_DOMAIN = "@kanchiuniv.ac.in";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResult registerManualUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            return AuthResult.failure("Username already exists!");
        }

        if (userRepository.existsByEmail(email)) {
            return AuthResult.failure("Email already exists!");
        }

        if (!email.endsWith(ALLOWED_DOMAIN)) {
            return AuthResult.failure("Email must end with @kanchiuniv.ac.in");
        }

        if (password.length() < 6 || password.length() > 8) {
            return AuthResult.failure("Password must be 6-8 characters");
        }

        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRegistration_mode(LoginModes.MANUAL);
        userRepository.save(user);

        return AuthResult.success("Registration successful");
    }
}