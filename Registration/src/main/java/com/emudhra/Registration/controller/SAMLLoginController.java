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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
@Deprecated
public class SAMLLoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/saml/success")
    public String samlSuccess(
            Saml2Authentication authentication,
            @AuthenticationPrincipal
            Saml2AuthenticatedPrincipal principal,
            HttpSession session,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        String email = principal.getFirstAttribute("email");
        String name = principal.getFirstAttribute("name");

        session.setAttribute("username", username);
        session.setAttribute("email", email);
        session.setAttribute("name", name);

        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("name", name);


        String registrationId =
                authentication.getSaml2Response();

            System.out.println("SAML Success Method Called");
            System.out.println("Authentication: " + authentication);
            System.out.println("Principal: " + principal);

            Users user = userService.findByEmail(email);

            if (user == null) {
                user = new Users();
                user.setEmail(email);
                user.setPassword("SAML_USER");
                user.setUsername(name);
                user.setRegistration_mode(LoginModes.SAML_SSO);
                userService.save(user);                    //save the user data in userdb
                userRepository.save(user);
            }

            LoginAudit loginAudit = new LoginAudit();

            loginAudit.setUser(user);
            loginAudit.setSessionId(session.getId());
            loginAudit.setLoginAt(LocalDateTime.now());
            loginAudit.setLoginMode(LoginModes.SAML_SSO);
            loginAuditRepository.save(loginAudit);                    //save the login data in login_audits_db;
            session.setAttribute(SessionAttribute.USER, user);

            model.addAttribute("id", user.getId());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("registration_mode", user.getRegistration_mode());
            model.addAttribute("registrationId", registrationId);

            System.out.println(principal.getAttributes());
            return "redirect:/dashboard";
    }
}
