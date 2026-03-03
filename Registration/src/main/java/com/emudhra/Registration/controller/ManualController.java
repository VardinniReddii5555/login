package com.emudhra.Registration.controller;

import com.emudhra.Registration.service.AuthResult;
import com.emudhra.Registration.service.RegistrationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManualController {
    private final RegistrationService registrationService;

    public ManualController(RegistrationService registrationService) {
        this.registrationService = registrationService;
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
            AuthResult result = registrationService.registerManualUser(username, email, password);
            if (!result.success()) {
                model.addAttribute("error", result.message());
                  return "register";
        }
        return "redirect:/login";
    }
}
