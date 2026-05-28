package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.service.OAuth2LoginSupport;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.emudhra.Registration.constants.SessionAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import static com.emudhra.Registration.constants.LoginModes.MANUAL;

@Controller
public class DashboardController {
    private final LoginAuditService loginAuditService;
    @Autowired
    private LoginAuditRepository loginAuditRepository;

    @Autowired
    private OAuth2LoginSupport support;

    public DashboardController(LoginAuditService loginAuditService) {
        this.loginAuditService = loginAuditService;
    }

    @GetMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal OAuth2User principal,
                                    @RequestParam(required = false) String loginMode,
                                    HttpSession session,
                                    Model model) {

        Object userObj = session.getAttribute(SessionAttribute.USER);
        if (!(userObj instanceof Users user)) {
            return "redirect:/login";
        }

        if(user != null) {
            LoginAudit loginAudit = loginAuditRepository.findTopByUserIdOrderByLoginAtDesc(user.getId());


            if (loginAudit != null) {
                loginMode = loginAudit.getLoginMode();
            }

            model.addAttribute("id", user.getId());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("registration_mode", user.getRegistration_mode());
            model.addAttribute("loginAudit", loginAudit);
            model.addAttribute("loginMode", loginMode);
            model.addAttribute("loginMode", loginAudit.getLoginMode());
        }

        String email = support.firstNonBlank(
                principal.getAttribute("email"),
                principal.getAttribute("mail"),
                principal.getAttribute("upn"),
                principal.getAttribute("preferred_username"),
                principal.getAttribute("unique_name"),
                principal.getAttribute("nameid"),
                principal.getName()
        );
        System.out.println("USER INFO → " + principal.getAttributes());

        model.addAttribute("user", user);
        model.addAttribute("loginMode", loginMode);
        model.addAttribute("selectedLoginMode", loginMode);
        model.addAttribute("loginModes", new String[]{
                LoginModes.MANUAL,
                LoginModes.FIREBASE_SSO,
                LoginModes.GOOGLE_SSO,
                LoginModes.GITHUB_SSO,
                LoginModes.OIDC_SSO,
                LoginModes.KEYCLOCK_OIDC,
                LoginModes.KEYCLOCK_SAML,
                LoginModes.SAML_SSO,
                LoginModes.DEFAULT
        });
        model.addAttribute("loginAudits", loginAuditService.fetchAudits(user.getId(), loginMode));
        model.addAttribute("loginMode", loginMode);
        return "dashboard";
    }
}