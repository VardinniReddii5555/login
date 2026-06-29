package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class ManualController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ManualController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }
    // 1. Serves the page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute Users user,
            @RequestParam(value = "enablePush", defaultValue = "false") boolean enablePush,
            Model model) {

        try {

            // Username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Username already exists!");
                return "register";
            }

            // Email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already exists!");
                return "register";
            }

            // Email validation
            if (!user.getEmail().endsWith("@emudhra.com")) {
                model.addAttribute("error", "Email must end with @emudhra.com");
                return "register";
            }

            // Manual registration
            if (!enablePush) {

                if (user.getPassword() == null ||
                        user.getPassword().length() < 6 ||
                        user.getPassword().length() > 8) {

                    model.addAttribute("error",
                            "Password must be 6-8 characters");
                    return "register";
                }

                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRegistration_mode(LoginModes.MANUAL);
                user.setPushRegistered(false);

            }
            // Push registration
            else {

                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setRegistration_mode(LoginModes.PUSH);
                user.setPushRegistered(true);
                user.setDeviceId(null);

            }

            userRepository.save(user);

            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server error occurred!");
            return "register";
        }
    }
}
