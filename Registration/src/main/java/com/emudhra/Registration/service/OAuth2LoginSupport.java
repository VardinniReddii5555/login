package com.emudhra.Registration.service;

import com.emudhra.Registration.model.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2LoginSupport {

    private final SocialLoginService socialLoginService;
    private final LoginAuditService loginAuditService;

    public OAuth2LoginSupport(SocialLoginService socialLoginService,
                              LoginAuditService loginAuditService) {
        this.socialLoginService = socialLoginService;
        this.loginAuditService = loginAuditService;
    }

    public String completeLogin(OAuth2User oauth2User,
                                HttpSession session,
                                String email,
                                String loginMode) {
        if (email == null || email.isBlank() || !email.endsWith("@online.emudhra.com")) {
            return "redirect:/login?oauth2Error=true";
        }

        Users user = socialLoginService.findOrCreateUser(
                email,
                firstNonBlank(
                        oauth2User.getAttribute("name"),
                        oauth2User.getAttribute("preferred_username"),
                        oauth2User.getAttribute("given_name"),
                        oauth2User.getAttribute("nickname")),
                firstNonBlank(
                        oauth2User.getAttribute("sub"),
                        oauth2User.getAttribute("id"),
                        oauth2User.getAttribute("uid")),
                loginMode);

        loginAuditService.startSessionAudit(user, session, loginMode);
        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    public String firstNonBlank(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            if (value == null) {
                continue;
            }
            String text = String.valueOf(value);
            if (!text.isBlank()) {
                return text;
            }
        }
        return null;
    }
}