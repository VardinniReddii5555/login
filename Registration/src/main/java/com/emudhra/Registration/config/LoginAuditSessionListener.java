package com.emudhra.Registration.config;

import com.emudhra.Registration.service.LoginAuditService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

@Component
public class LoginAuditSessionListener implements HttpSessionListener {

    private final LoginAuditService loginAuditService;

    public LoginAuditSessionListener(LoginAuditService loginAuditService) {
        this.loginAuditService = loginAuditService;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        loginAuditService.closeSessionAudit(session);
    }
}