package com.emudhra.Registration.config;

import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class OidcAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final LoginAuditRepository loginAuditRepository;

    public OidcAuthenticationSuccessHandler(UserRepository userRepository,
                                            LoginAuditRepository loginAuditRepository) {
        this.userRepository = userRepository;
        this.loginAuditRepository = loginAuditRepository;

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String username = oidcUser.getPreferredUsername();

        Users user = userRepository.findByUsername(username);

        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setSessionId(request.getSession().getId());
        audit.setLoginAt(LocalDateTime.now());
        audit.setLoginMode("OIDC_KEYCLOAK");

        loginAuditRepository.save(audit);

        setDefaultTargetUrl("/dashboard");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}