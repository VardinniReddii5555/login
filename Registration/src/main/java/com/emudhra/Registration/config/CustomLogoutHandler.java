package com.emudhra.Registration.config;

import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.repository.LoginAuditRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomLogoutHandler implements LogoutSuccessHandler {

    private final LoginAuditRepository loginAuditRepository;

    public CustomLogoutHandler(LoginAuditRepository loginAuditRepository) {
        this.loginAuditRepository = loginAuditRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {

        String sessionId = request.getSession().getId();

        LoginAudit audit = loginAuditRepository.findBySessionId(sessionId);

        if (audit != null) {

            LocalDateTime logoutTime = LocalDateTime.now();

            audit.setLogoutAt(logoutTime);

            long duration = java.time.Duration.between(
                    audit.getLoginAt(), logoutTime).getSeconds();

            audit.setSessionDurationSeconds(duration);

            loginAuditRepository.save(audit);
        }

        response.sendRedirect("/");
    }
}
