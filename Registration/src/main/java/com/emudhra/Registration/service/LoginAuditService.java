package com.emudhra.Registration.service;

import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAuditService {

    private final LoginAuditRepository loginAuditRepository;

    public LoginAuditService(LoginAuditRepository loginAuditRepository) {
        this.loginAuditRepository = loginAuditRepository;
    }

    public void startSessionAudit(Users user, HttpSession session, String loginMode) {
        session.setAttribute(SessionAttribute.USER, user);

        LoginAudit loginAudit = new LoginAudit();
        loginAudit.setUser(user);
        loginAudit.setSessionId(session.getId());
        loginAudit.setLoginAt(LocalDateTime.now());
        loginAudit.setLoginMode(loginMode);
//        loginAudit.setLoginMode(loginAudit.getLoginMode());

        System.out.println("Login mode is: " + loginMode);
        LoginAudit savedAudit = loginAuditRepository.save(loginAudit);
        session.setAttribute(SessionAttribute.ACTIVE_LOGIN_AUDIT_ID, savedAudit.getId());
    }

    public void closeSessionAudit(HttpSession session) {
        Object loginAuditId = session.getAttribute(SessionAttribute.ACTIVE_LOGIN_AUDIT_ID);

        if (loginAuditId instanceof Long auditId) {
            loginAuditRepository.findById(auditId).ifPresent(this::markLogoutIfRequired);
            return;
        }

        Object userObj = session.getAttribute(SessionAttribute.USER);
        if (userObj instanceof Users user) {
            loginAuditRepository.findTopByUserIdAndLogoutAtIsNullOrderByLoginAtDesc(user.getId())
                    .ifPresent(this::markLogoutIfRequired);
        }
    }

    private void markLogoutIfRequired(LoginAudit loginAudit) {
        if (loginAudit.getLogoutAt() == null) {
            loginAudit.markLogout(LocalDateTime.now());
            loginAuditRepository.save(loginAudit);
        }
    }
}