package com.emudhra.Registration.controller;

import com.emudhra.Registration.service.UserService;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.service.EmudhraTokenValidationService;
import com.emudhra.Registration.service.OAuth2LoginSupport;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class EmudhraLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private EmudhraTokenValidationService tokenValidationService;
    @Autowired
    private OAuth2LoginSupport support;

    public String handle(OAuth2User oauth2User,
                         OAuth2AuthenticationToken authentication,
                         HttpSession session) {

        try {
            System.out.println("🔥 EMUDHRA LOGIN HANDLER HIT");

            boolean valid = tokenValidationService.isValid(authentication, oauth2User);

            System.out.println("Token validation result: " + valid);

            if (!valid) {
                return "redirect:/login?invalidToken=true";
            }

            // Extract user info safely
            String email = support.firstNonBlank(
                    oauth2User.getAttribute("email"),
                    oauth2User.getAttribute("mail"),
                    oauth2User.getAttribute("upn"),
                    oauth2User.getAttribute("preferred_username"),
                    oauth2User.getAttribute("unique_name"),
                    oauth2User.getAttribute("nameid"),
                    authentication.getName());
            if (email == null) {
                email = oauth2User.getAttribute("sub"); // fallback
            }
            return "dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?oauth2Error=true";
        }
    }
}