package com.emudhra.Registration.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public HomeController(UserRepository userRepository) {
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

            String encryptedPassword = passwordEncoder.encode(password);
            Users users = new Users(username, email, encryptedPassword);
            userRepository.save(users);

            return "redirect:/success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server error occurred!");
            return "register";
        }
    }
    @GetMapping("/success")
    public String successPage() {
        return "success";
    }
}
