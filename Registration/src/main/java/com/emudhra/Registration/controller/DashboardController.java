package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.service.OAuth2LoginSupport;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {
    @Autowired
    private LoginAuditService loginAuditService;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private OAuth2LoginSupport support;

    @GetMapping("/dashboard")
    public String showDashboardPage(Authentication authentication, HttpServletRequest request, @RequestParam(required = false) String loginMode, HttpSession session, Model model) {
        Users user = null;
        String email = null;
        Object userObj = session.getAttribute(SessionAttribute.USER);
        if (userObj instanceof Users) {
            user = (Users) userObj;
        }
        session.setAttribute(SessionAttribute.USER, user);
        if(user == null){
            System.out.println("SESSION USER IS NULL");
            return "redirect:/login";
        }

        Long userId = user.getId();
        if (user != null) {
            System.out.println("SESSION USER = " + user.getEmail());
        } else {
            System.out.println("SESSION USER IS NULL");
        }
        LoginAudit loginAudit = loginAuditRepository.findTopByUserIdOrderByLoginAtDesc(user.getId());

        if (loginAudit != null) {
            loginMode = loginAudit.getLoginMode();
        }

        System.out.println("AUTH = " + authentication);
        System.out.println("SESSION USER => " + session.getAttribute(SessionAttribute.USER));
        System.out.println("AUTH => " + authentication);
        System.out.println("SESSION USER EMAIL => " + user.getEmail());

        model.addAttribute("id", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("registration_mode", user.getRegistration_mode());
        model.addAttribute("user", user);
        model.addAttribute("loginAudit", loginAudit);

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User principal = oauthToken.getPrincipal();
            System.out.println("OAUTH ATTRIBUTES => " + principal.getAttributes());
            principal.getAttributes()
                    .forEach((k, v) -> System.out.println(k + " = " + v));
            email = support.firstNonBlank(
                    principal.getAttribute("email"),
                    principal.getAttribute("mail"),
                    principal.getAttribute("upn"),
                    principal.getAttribute("preferred_username"),
                    principal.getAttribute("unique_name"),
                    principal.getAttribute("nameid"),
                    principal.getName());
            model.addAttribute("oauthAttributes", principal.getAttributes());
        } else if (authentication instanceof Saml2Authentication samlAuth) {
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) samlAuth.getPrincipal();
            System.out.println("SAML ATTRIBUTES => " + principal.getAttributes());
            email = support.firstNonBlank(
                    principal.getFirstAttribute("email"),
                    principal.getFirstAttribute("mail"),
                    principal.getFirstAttribute("upn"),
                    principal.getFirstAttribute("preferred_username"),
                    principal.getFirstAttribute("unique_name"),
                    principal.getFirstAttribute("nameid"),
                    principal.getName());
            model.addAttribute("samlAttributes", principal.getAttributes());
            model.addAttribute("samlResponse", samlAuth.getSaml2Response());
        }
        System.out.println("EMAIL = " + email);
        model.addAttribute("loginMode", loginMode);
        model.addAttribute("selectedLoginMode", loginMode);
        model.addAttribute("loginModes", new String[]{
                LoginModes.MANUAL,
                LoginModes.SMTP_AUTH,
                LoginModes.FIREBASE_SSO,
                LoginModes.GOOGLE_SSO,
                LoginModes.GITHUB_SSO,
                LoginModes.OIDC_SSO,
                LoginModes.KEYCLOCK_OIDC,
                LoginModes.KEYCLOCK_SAML,
                LoginModes.SAML_SSO,
                LoginModes.DEFAULT});
        model.addAttribute("loginAudits", loginAuditService.fetchAudits(user.getId(), loginMode));
        System.out.println("SESSION USER => " + session.getAttribute(SessionAttribute.USER));
//        System.out.println("header_id => " + request.getHeader("X-User-Id"));
//        System.out.println("header_username => " + request.getHeader("X-User-Name"));
//        System.out.println("header_firstname => " + request.getHeader("X-First-Name"));
//        System.out.println("header_lastname => " + request.getHeader("X-Last-Name"));
//        System.out.println("header_email => " + request.getHeader("X-User-Email"));
//        System.out.println("header_user_roles => " + request.getHeader("X-User-Roles"));
        return "dashboard";
    }
}