package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import com.emudhra.Registration.constants.SessionAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    private final LoginAuditService loginAuditService;
    public DashboardController(LoginAuditService loginAuditService,
                               UserService userService) {
        this.loginAuditService = loginAuditService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null) {
            return "login"; // ok fallback
        }

        String email = principal.getAttribute("email");
        model.addAttribute("email", email);

        return "dashboard";
    }
}