package com.emudhra.Registration.config;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.service.AuthResult;
import com.emudhra.Registration.service.SsoLoginService;
import com.emudhra.Registration.config.SecurityConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OidcAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SsoLoginService ssoLoginService;
    private final SecurityConfig securityConfig;

    public OidcAuthenticationSuccessHandler(SsoLoginService ssoLoginService , SecurityConfig securityConfig) {
        this.ssoLoginService = ssoLoginService;
        this.securityConfig = securityConfig;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)
                || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            response.sendRedirect(request.getContextPath() + "/login?error=unsupported_login_type");
            return;
        }

        String loginMode = resolveLoginMode(oauthToken.getAuthorizedClientRegistrationId());
        HttpSession session = request.getSession(true);
        AuthResult result = ssoLoginService.loginWithOidc(oidcUser, session, loginMode);

        if (!result.success()) {
            response.sendRedirect(request.getContextPath() + "/login?error=" + result.message().replace(" ", "+"));
            return;
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private String resolveLoginMode(String registrationId) {
        if ("keycloak".equalsIgnoreCase(registrationId)) {
            return LoginModes.KEYCLOAK;
        }
        return LoginModes.OIDC_OAUTH2;
    }
}