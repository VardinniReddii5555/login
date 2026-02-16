package com.emudhra.Registration.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
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
    @ResponseBody
    public Map<String, String> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        Map<String, String> response = new HashMap<>();

        try {

            if (userRepository.existsByUsername(username)) {
                response.put("status", "ERROR");
                response.put("message", "Username already exists!");
                return response;
            }

            if (userRepository.existsByEmail(email)) {
                response.put("status", "ERROR");
                response.put("message", "Email already exists!");
                return response;
            }

            String encryptedPassword = passwordEncoder.encode(password);
            Users users = new Users(username, email, encryptedPassword);
            userRepository.save(users);
            System.out.println("User saved successfully");
            response.put("status", "SUCCESS");
            response.put("message", "Registration successful!");
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "Server error occurred!");
            return response;
        }
    }
    @GetMapping("/success")
    public String successPage() {
        return "success";
    }


}
