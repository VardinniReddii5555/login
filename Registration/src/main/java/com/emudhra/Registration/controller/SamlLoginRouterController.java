package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.repository.UserRepository;
import com.emudhra.Registration.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class SamlLoginRouterController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/saml/success")
    public String samlLoginSuccess(
            Saml2Authentication authentication,
            @AuthenticationPrincipal Saml2AuthenticatedPrincipal principal,
            HttpSession session,
            Model model) {

        if (!(authentication instanceof Saml2Authentication samlAuth)) {return "redirect:/login";}
        else {
            String registrationId = samlAuth.getSaml2Response();
            System.out.println(registrationId);
        }

        String email =
                Optional.ofNullable(
                        principal.getFirstAttribute("email")
                ).orElse(principal.getName()).toString();
        if (email == null) {email = principal.getName();}
        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            user = new Users();
            user.setEmail(email);
            user.setUsername(principal.getName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            usersRepository.save(user);
        }
        session.setAttribute(SessionAttribute.USER, user);
        String registrationId = "keycloak";
        System.out.println("OAuth2 Success Method Called");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + principal);
        String loginMode = switch (registrationId.toLowerCase()) {
            case "emudhra" -> LoginModes.OIDC_SSO;
            case "google" -> LoginModes.GOOGLE_SSO;
            case "github" -> LoginModes.GITHUB_SSO;
            case "firebase" -> LoginModes.FIREBASE_SSO;
            case "saml" -> LoginModes.SAML_SSO;
            case "employee-portal" -> LoginModes.KEYCLOCK_OIDC;
            case "employee-portal-2" -> LoginModes.KEYCLOCK_SAML;
            default -> LoginModes.DEFAULT;
        };
        if (user == null) {

            user = new Users();

            user.setEmail(email);
            user.setUsername(principal.getName());
            user.setPassword(passwordEncoder.encode("SAML_USER"));
            user.setRegistration_mode(LoginModes.KEYCLOCK_SAML);
            usersRepository.save(user);
        }
        LoginAudit loginAudit = new LoginAudit();
        loginAudit.setUser(user);
        loginAudit.setSessionId(session.getId());
        loginAudit.setLoginAt(LocalDateTime.now());
        loginAudit.setLoginMode(loginMode);
        loginAuditRepository.save(loginAudit);
        session.setAttribute(SessionAttribute.USER, user);
        model.addAttribute("id", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("registration_mode", user.getRegistration_mode());
        model.addAttribute("loginMode",loginAudit.getLoginMode());
        return "redirect:/dashboard";
    }
}