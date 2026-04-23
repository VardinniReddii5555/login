package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import com.emudhra.Registration.service.LoginAuditService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller

public class ManualLoginController {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(1);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAuditService loginAuditService;

    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedUntil = new ConcurrentHashMap<>();

    @Autowired
    public ManualLoginController(PasswordEncoder passwordEncoder,
                                 UserRepository userRepository,
                                 LoginAuditService loginAuditService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.loginAuditService = loginAuditService;
    }

    @GetMapping({"", "/", "/login"})
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model,
                            HttpSession session) {
        if (isBlocked(username, model)) {
            return "login";
        }

        Users user = userRepository.findByUsername(username);
        //Username not found
        if (user == null) {
            model.addAttribute("error", "Username not found. Please register.");
            return "login";
        }
        //Password mismatch
        if (!passwordEncoder.matches(password, user.getPassword())) {
            applyFailedAttempt(username, model);
            return "login";
        }
        //Success
        failedAttempts.remove(username);
        blockedUntil.remove(username);
        loginAuditService.startSessionAudit(user, session, LoginModes.MANUAL);
        session.setAttribute("user", user);

        // 🔥 LOGIN AUDIT
        loginAuditService.startSessionAudit(user, session, LoginModes.MANUAL);

        // ✅ SEND TO DASHBOARD
        model.addAttribute("id", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("registration_mode", user.getRegistration_mode());

        return "dashboard";
    }
    private boolean isBlocked(String username, Model model) {
        Instant now = Instant.now();
        Instant blockExpiry = blockedUntil.get(username);
        if (blockExpiry == null) {
            return false;
        }
        if (now.isBefore(blockExpiry)) {
            long remainingSeconds = Duration.between(now, blockExpiry).toSeconds();
            model.addAttribute("error", "Too many wrong attempts. Try again in " + remainingSeconds + " seconds.");
            return true;
        }
        blockedUntil.remove(username);
        failedAttempts.remove(username);
        return false;
    }
    private void applyFailedAttempt(String username, Model model) {
        int attempts = failedAttempts.getOrDefault(username, 0) + 1;
        failedAttempts.put(username, attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            blockedUntil.put(username, Instant.now().plus(BLOCK_DURATION));
            failedAttempts.remove(username);
            model.addAttribute("error", "Password incorrect. Account blocked for 1 minute.");
            return;
        }
        model.addAttribute("error", "Password incorrect. " + (MAX_FAILED_ATTEMPTS - attempts) + " attempt(s) left.");
    }
}