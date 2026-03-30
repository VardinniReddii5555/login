package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2Controller {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAuditService loginAuditService;

    public OAuth2Controller(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            LoginAuditService loginAuditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAuditService = loginAuditService;
    }

    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User, HttpSession session) {
        if (oauth2User == null) {
            return "redirect:/login?oauth2Error=true";
        }

        String email = oauth2User.getAttribute("email");
        if (email == null || !email.endsWith("@kanchiuniv.ac.in")) {
            return "redirect:/login?oauth2Error=true";
        }

        String name = oauth2User.getAttribute("name");
        String preferredUsername = (name != null && !name.isBlank())
                ? name.replaceAll("\\s+", "")
                : email.split("@")[0];

        if (preferredUsername.length() > 50) {
            preferredUsername = preferredUsername.substring(0, 50);
        }

        Users existingUser = userRepository.findByEmail(email);
        Users activeUser;

        if (existingUser == null) {
            String username = preferredUsername;
            int suffix = 1;
            while (userRepository.existsByUsername(username)) {
                String suffixValue = String.valueOf(suffix);
                int maxBaseLength = 50 - suffixValue.length();
                String base = preferredUsername.length() > maxBaseLength
                        ? preferredUsername.substring(0, maxBaseLength)
                        : preferredUsername;
                username = base + suffixValue;
                suffix++;
            }

            String sub = oauth2User.getAttribute("sub");
            String generatedPassword = passwordEncoder.encode(sub != null ? sub : email);
            Users user = new Users();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(generatedPassword);
            user.setRegistration_mode(LoginModes.GOOGLE_SSO);
            activeUser = userRepository.save(user);
        } else {
            existingUser.setRegistration_mode(LoginModes.GOOGLE_SSO);
            activeUser = userRepository.save(existingUser);
        }

        loginAuditService.startSessionAudit(activeUser, session, LoginModes.GOOGLE_SSO);
        session.setAttribute("user", activeUser);

        return "redirect:/dashboard";
    }
}