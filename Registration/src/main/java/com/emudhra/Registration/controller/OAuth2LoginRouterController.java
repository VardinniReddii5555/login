package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.model.LoginAudit;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class OAuth2LoginRouterController {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private GoogleLoginController googleLoginController;
    @Autowired
    private GithubLoginController githubLoginController;
    @Autowired
    private EmudhraLoginController emudhraLoginController;
    @Autowired
    private OAuth2AuthorizedClientService clientService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public OAuth2LoginRouterController(GoogleLoginController googleLoginController,
                                       GithubLoginController githubLoginController,
                                       EmudhraLoginController emudhraLoginController) {
        this.googleLoginController = googleLoginController;
        this.githubLoginController = githubLoginController;
        this.emudhraLoginController = emudhraLoginController;
    }

    @GetMapping("/oauth2/success")
    public String oauth2Success(OAuth2AuthenticationToken authentication,
                                @AuthenticationPrincipal OAuth2User principal,
                                HttpSession session,
                                Model model ) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {

            Map<String, Object> attributes = principal.getAttributes();
            System.out.println("OAuth Attributes: " + attributes);
            String email = null;
            String name = "User";

            if (attributes.get("email") != null)
            {
                email = attributes.get("email").toString();
            }
            else if (attributes.get("preferred_username") != null)
            {
                email = attributes.get("preferred_username").toString();
            }
            else if (attributes.get("sub") != null)
            {
                email = attributes.get("sub").toString(); // fallback
            }

            if (attributes.get("name") != null) {
                name = attributes.get("name").toString();
            }

            if (email == null) {
                throw new RuntimeException("Email not found from OAuth provider");
            }
            String registrationId;
            registrationId = authentication.getAuthorizedClientRegistrationId();
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

            Users user = userService.findByEmail(email);

            if (user == null) {
                user = new Users();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(email));
                user.setUsername(name);
                user.setRegistration_mode(loginMode);

                userService.save(user);
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

            // 🎯 Success page
            return "redirect:/dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}