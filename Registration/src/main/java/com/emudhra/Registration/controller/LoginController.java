package com.emudhra.Registration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import com.emudhra.Registration.service.AuthResult;
import com.emudhra.Registration.service.ManualLoginService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final ManualLoginService manualLoginService;
    public LoginController(ManualLoginService manualLoginService) {
        this.manualLoginService = manualLoginService;
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
        AuthResult result = manualLoginService.login(username, password, session);
        if (!result.success()) {
            model.addAttribute("error", result.message());
            return "login";
        }
        return "redirect:/dashboard";
    }
}
