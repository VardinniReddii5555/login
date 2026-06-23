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

        String email = Optional.ofNullable(
                principal.getFirstAttribute("email")
                        ).orElse(principal.getName()).toString();

        System.out.println("SAML EMAIL = " + email);

        Users user = usersRepository.findByEmail(email);
        String loginMode = LoginModes.KEYCLOCK_SAML;

        if (user == null) {
            user = new Users();
            user.setEmail(email);
            user.setUsername(principal.getName());
            user.setPassword(passwordEncoder.encode(email));
            user.setRegistration_mode(loginMode);
            user = usersRepository.save(user);
            System.out.println("USER CREATED = " + user.getId());
        }

        session.setAttribute(SessionAttribute.USER, user);

        System.out.println("SESSION USER SAVED = " + session.getAttribute(SessionAttribute.USER));
        LoginAudit loginAudit = new LoginAudit();

        loginAudit.setUser(user);
        loginAudit.setSessionId(session.getId());
        loginAudit.setLoginAt(LocalDateTime.now());
        loginAudit.setLoginMode(loginMode);

        loginAuditRepository.save(loginAudit);

        model.addAttribute("id", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("registration_mode", user.getRegistration_mode());
        model.addAttribute("loginMode", loginMode);
        System.out.println("SESSION USER SAVED => " + session.getAttribute(SessionAttribute.USER));
        System.out.println("REDIRECTING TO DASHBOARD");


        return "redirect:/dashboard";
    }
}