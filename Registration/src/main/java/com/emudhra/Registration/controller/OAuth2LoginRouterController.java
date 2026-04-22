package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.service.UserService;
import com.emudhra.Registration.service.OAuth2LoginSupport;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Map;

//@Controller
//public class OAuth2LoginRouterController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private LoginAuditService auditService;
//
//    @Autowired
//    private OAuth2LoginSupport support;
//
//    @GetMapping("/oauth2/success")
//    public String oauth2Success(OAuth2AuthenticationToken authentication,
//                                @AuthenticationPrincipal OAuth2User principal,
//                                HttpSession session,
//                                Model model) {
//
//        System.out.println("🔥 OAuth2 SUCCESS ROUTER HIT");
//
//        if (principal == null) {
//            return "redirect:/login";
//        }
//
//        Map<String, Object> attrs = principal.getAttributes();
//
//        System.out.println("USER ATTRIBUTES 👉 " + attrs);
//
//        // ✅ Extract email safely (multi-provider support)
//        String email = support.firstNonBlank(
//                (String) attrs.get("email"),
//                (String) attrs.get("mail"),
//                (String) attrs.get("upn"),
//                (String) attrs.get("preferred_username"),
//                (String) attrs.get("unique_name"),
//                (String) attrs.get("nameid"),
//                principal.getName()
//        );
//
//        // ✅ Extract username
//        String username = support.firstNonBlank(
//                (String) attrs.get("name"),
//                (String) attrs.get("preferred_username"),
//                email
//        );
//
//        // ❌ If no email → stop
//        if (email == null) {
//            System.out.println("❌ Email not found from provider");
//            return "redirect:/login";
//        }
//
//        // ✅ Find or create user
//        Users user = userService.findByEmail(email);
//
//        if (user == null) {
//            user = new Users();
//            user.setEmail(email);
//            user.setUsername(username);
//            user.setRegistration_mode("OAUTH");
//
//            userService.save(user);
//
//            System.out.println("✅ New user created: " + email);
//        } else {
//            System.out.println("✅ Existing user found: " + email);
//        }
//
//        // ✅ Store login audit
//        auditService.startSessionAudit(user, session, "OAUTH");
//
//        // ✅ Send data to UI
//        model.addAttribute("id", user.getId());
//        model.addAttribute("name", user.getUsername());
//        model.addAttribute("email", user.getEmail());
//        model.addAttribute("mode", user.getRegistration_mode());
//
//        return "dashboard";
//    }
//}


@Controller
public class OAuth2LoginRouterController {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User principal, HttpSession session, Model model) {

        // 🔐 If not logged in
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // ✅ Get all attributes from OAuth provider
            Map<String, Object> attributes = principal.getAttributes();

            // 🔥 Print attributes (for debugging - REMOVE later)
            System.out.println("OAuth Attributes: " + attributes);

            // ✅ Extract values safely
            String email = null;
            String name = "User";

            // 🔁 Handle different providers (Emudhra / Google etc.)
            if (attributes.get("email") != null) {
                email = attributes.get("email").toString();
            } else if (attributes.get("preferred_username") != null) {
                email = attributes.get("preferred_username").toString();
            } else if (attributes.get("sub") != null) {
                email = attributes.get("sub").toString(); // fallback
            }

            if (attributes.get("name") != null) {
                name = attributes.get("name").toString();
            }

            // ❌ Critical check
            if (email == null) {
                throw new RuntimeException("Email not found from OAuth provider");
            }

            // ✅ Check if user exists
            Users user = userService.findByEmail(email);

            // 🆕 Create new user if not exists
            if (user == null) {
                user = new Users();
                user.setEmail(email);
                user.setPassword("OAUTH_USER");
                user.setUsername(name);
                user.setRegistration_mode(LoginModes.EMUDHRA_SSO);

                userService.save(user);
            }

            // 🔥 LOGIN AUDIT
            LoginAudit loginAudit = new LoginAudit();

            loginAudit.setUser(user);
//            loginAudit.setEmail(user.getEmail());
            loginAudit.setSessionId(session.getId());
            loginAudit.setLoginAt(LocalDateTime.now());
            loginAudit.setLoginMode(LoginModes.EMUDHRA_SSO);

            loginAuditRepository.save(loginAudit);
            // ✅ Pass data to UI
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());

            // 🎯 Success page
            return "dashboard";

        } catch (Exception e) {
            // ❌ Print error in console
            e.printStackTrace();

            // 👉 Send error to UI
            model.addAttribute("errorMessage", e.getMessage());

            return "error";
        }
    }
}