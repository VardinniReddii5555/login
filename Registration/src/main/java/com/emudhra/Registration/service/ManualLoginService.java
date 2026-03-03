package com.emudhra.Registration.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class ManualLoginService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(1);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAuditService loginAuditService;

    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedUntil = new ConcurrentHashMap<>();

    public ManualLoginService(PasswordEncoder passwordEncoder,
                              UserRepository userRepository,
                              LoginAuditService loginAuditService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.loginAuditService = loginAuditService;
    }

    public AuthResult login(String username, String password, HttpSession session) {
        Instant currentTime = Instant.now();
        Instant blockExpiry = blockedUntil.get(username);

        if (blockExpiry != null && currentTime.isBefore(blockExpiry)) {
            long remainingSeconds = Duration.between(currentTime, blockExpiry).toSeconds();
            return AuthResult.failure("Too many wrong attempts. Try again in " + remainingSeconds + " seconds.");
        }

        if (blockExpiry != null && !currentTime.isBefore(blockExpiry)) {
            blockedUntil.remove(username);
            failedAttempts.remove(username);
        }

        Users user = userRepository.findByUsername(username);
        if (user == null) {
            return AuthResult.failure("Username not found. Please register.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            int attempts = failedAttempts.getOrDefault(username, 0) + 1;
            failedAttempts.put(username, attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                blockedUntil.put(username, currentTime.plus(BLOCK_DURATION));
                failedAttempts.remove(username);
                return AuthResult.failure("Password incorrect. Account blocked for 1 minute.");
            }

            int remainingAttempts = MAX_FAILED_ATTEMPTS - attempts;
            return AuthResult.failure("Password incorrect. " + remainingAttempts + " attempt(s) left.");
        }

        failedAttempts.remove(username);
        blockedUntil.remove(username);
        loginAuditService.startSessionAudit(user, session, LoginModes.MANUAL);
        session.setAttribute("user", user);
        return AuthResult.success("Login successful");
    }
}