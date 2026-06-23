package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.service.LoginAuditService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {
    private final LoginAuditService loginAuditService;

    public LogoutController(LoginAuditService loginAuditService) {
        this.loginAuditService = loginAuditService;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        loginAuditService.closeSessionAudit(session);
        session.invalidate();
        System.out.println("Session ID: " + session.getId());
        System.out.println("Audit ID: " + session.getAttribute(SessionAttribute.ACTIVE_LOGIN_AUDIT_ID));
        return "redirect:/login?logot=true";
    }

}
