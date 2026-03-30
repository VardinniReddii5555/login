package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.service.LoginAuditService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.emudhra.Registration.constants.SessionAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {
    private final LoginAuditService loginAuditService;

    public DashboardController(LoginAuditService loginAuditService) {
        this.loginAuditService = loginAuditService;
    }

    @GetMapping("/dashboard")
    public String showDashboardPage(@RequestParam(required = false) String loginMode,
                                    HttpSession session,
                                    Model model) {
        Object userObj = session.getAttribute(SessionAttribute.USER);
        if (!(userObj instanceof Users user)) {
            return "redirect:/login";
        }

        model.addAttribute("selectedLoginMode", loginMode);
        model.addAttribute("loginModes", new String[]{
                LoginModes.MANUAL,
                LoginModes.FIREBASE_SSO,
                LoginModes.GOOGLE_SSO,
                LoginModes.GITHUB_SSO,
                LoginModes.EMUDHRA_SSO
        });
        model.addAttribute("loginAudits", loginAuditService.fetchAudits(user.getId(), loginMode));
        return "dashboard";
    }
}