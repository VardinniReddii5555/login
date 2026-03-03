package com.emudhra.Registration.controller;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class ManualController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ManualController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        try {

            if (userRepository.existsByUsername(username)) {
                model.addAttribute("error", "Username already exists!");
                return "register";
            }

            if (userRepository.existsByEmail(email)) {
                model.addAttribute("error", "Email already exists!");
                return "register";
            }

            if (!email.endsWith("@kanchiuniv.ac.in")) {
                model.addAttribute("error", "Email must end with @kanchiuniv.ac.in");
                return "register";
            }

            if (password.length() < 6 || password.length() > 8) {
                model.addAttribute("error", "Password must be 6-8 characters");
                return "register";
            }

            String encryptedPassword = passwordEncoder.encode(password);

            Users users = new Users();
            users.setUsername(username);
            users.setEmail(email);
            users.setPassword(encryptedPassword);
            users.setRegistration_mode("MANUAL");
            userRepository.save(users);
            userRepository.save(users);

            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server error occurred!");
            return "register";
        }
    }
}
