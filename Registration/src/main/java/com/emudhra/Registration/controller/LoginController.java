package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import com.emudhra.Registration.service.LoginAuditService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class LoginController {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(1);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAuditService loginAuditService;

    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedUntil = new ConcurrentHashMap<>();

    @Autowired
    public LoginController(PasswordEncoder passwordEncoder, UserRepository userRepository, LoginAuditService loginAuditService) {
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

        Instant currentTime = Instant.now();
        Instant blockExpiry = blockedUntil.get(username);

        if (blockExpiry != null && currentTime.isBefore(blockExpiry)) {
            long remainingSeconds = Duration.between(currentTime, blockExpiry).toSeconds();
            model.addAttribute("error", "Too many wrong attempts. Try again in " + remainingSeconds + " seconds.");
            return "login";
        }

        if (blockExpiry != null && !currentTime.isBefore(blockExpiry)) {
            blockedUntil.remove(username);
            failedAttempts.remove(username);
        }

        Users user = userRepository.findByUsername(username);
//        Users user = userRepository.findByEmail(username);

        // 1️⃣ Username not found
        if (user == null) {
            model.addAttribute("error", "Username not found. Please register.");
            return "login";
        }

        // 2️⃣ Password mismatch
        if (!passwordEncoder.matches(password, user.getPassword())) {
            int attempts = failedAttempts.getOrDefault(username, 0) + 1;
            failedAttempts.put(username, attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                blockedUntil.put(username, currentTime.plus(BLOCK_DURATION));
                failedAttempts.remove(username);
                model.addAttribute("error", "Password incorrect. Account blocked for 1 minute.");
                return "login";
            }

            int remainingAttempts = MAX_FAILED_ATTEMPTS - attempts;
            model.addAttribute("error", "Password incorrect. " + remainingAttempts + " attempt(s) left.");
            return "login";
        }

        // 3️⃣ Success
        failedAttempts.remove(username);
        blockedUntil.remove(username);
        loginAuditService.startSessionAudit(user, session, LoginModes.MANUAL);

            session.setAttribute("user", user);
            return "redirect:/dashboard";
    }
}
