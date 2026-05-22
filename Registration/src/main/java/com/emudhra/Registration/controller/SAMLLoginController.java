package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SAMLLoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginAuditService loginAuditService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/saml/success")
    public String samlSuccess(Authentication authentication,
                              @AuthenticationPrincipal OAuth2User principal,
                              HttpSession session,
                              Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        Saml2AuthenticatedPrincipal principal =
                (Saml2AuthenticatedPrincipal) authentication.getPrincipal();

        // 🔥 Print all attributes (VERY IMPORTANT for debugging)
        principal.getAttributes().forEach((key, value) ->
                System.out.println("SAML ATTR 👉 " + key + " : " + value)
        );

        // ✅ Extract NameID
        String username = principal.getName();
        System.out.println("🔥 NameID (username): " + username);


        // ✅ Example attribute extraction (depends on IdP)
        String email = principal.getFirstAttribute("email");
        System.out.println("🔥 EMAIL: " + email);

        if (email == null) {
            email = username;
        }
        // 💾 Store in session
        session.setAttribute("username", username);
        session.setAttribute("email", email);

        // 🎯 Send to UI
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        Users existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            userService.save(existingUser);
        }
        loginAuditService.startSessionAudit(existingUser,session, LoginModes.SAML_SSO);

        return "dashboard";
    }
}
