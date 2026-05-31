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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.emudhra.Registration.constants.SessionAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;

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
        public String showDashboardPage(Authentication authentication,
                                        @RequestParam(required = false) String loginMode,
                                        HttpSession session,
                                        Model model) {

            Object userObj = session.getAttribute(SessionAttribute.USER);
            Users user = null;
            if (userObj instanceof Users) {
                user = (Users) userObj;
            }
            if (user != null) {
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
            String email = null;
            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                OAuth2User principal1 = oauthToken.getPrincipal();
                System.out.println("OIDC USER INFO => " + principal1.getAttributes());
                email = support.firstNonBlank(
                        principal1.getAttribute("email"),
                        principal1.getAttribute("mail"),
                        principal1.getAttribute("upn"),
                        principal1.getAttribute("preferred_username"),
                        principal1.getAttribute("unique_name"),
                        principal1.getAttribute("nameid"),
                        principal1.getName()
                );
            }
            else if (authentication instanceof Saml2Authentication samlAuth) {
                Saml2AuthenticatedPrincipal principal2 = (Saml2AuthenticatedPrincipal) samlAuth.getPrincipal();
                email = support.firstNonBlank(
                        principal2.getFirstAttribute("email"),
                        principal2.getFirstAttribute("mail"),
                        principal2.getFirstAttribute("upn"),
                        principal2.getFirstAttribute("preferred_username"),
                        principal2.getFirstAttribute("unique_name"),
                        principal2.getAttribute("nameid"),
                        principal2.getName());
                model.addAttribute("samlResponse", samlAuth.getSaml2Response());
                System.out.println("SAML ATTRIBUTES => " + principal2.getAttributes());
                System.out.println("USER INFO → " + principal2.getAttributes());
                if (user == null) {
                    model.addAttribute("samlUser", principal2.getName());
                    model.addAttribute("samlAttributes", principal2.getAttributes());
                    return "dashboard";
                }
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
            }
                model.addAttribute("loginAudits", loginAuditService.fetchAudits(user.getId(), loginMode));
                model.addAttribute("loginMode", loginMode);
                return "dashboard";
            }
        }
