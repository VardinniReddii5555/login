package com.emudhra.Registration.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2LoginRouterController {

    private final GoogleLoginController googleLoginController;
    private final GithubLoginController githubLoginController;
    private final EmudhraLoginController emudhraLoginController;

    public OAuth2LoginRouterController(GoogleLoginController googleLoginController,
                                       GithubLoginController githubLoginController,
                                       EmudhraLoginController emudhraLoginController) {
        this.googleLoginController = googleLoginController;
        this.githubLoginController = githubLoginController;
        this.emudhraLoginController = emudhraLoginController;
    }

    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User,
                                OAuth2AuthenticationToken authentication,
                                HttpSession session) {
        if (oauth2User == null || authentication == null) {
            return "redirect:/dashboard";
//                    "login?oauth2Error=true";
        }

        return switch (authentication.getAuthorizedClientRegistrationId().toLowerCase()) {
            case "google" -> googleLoginController.handle(oauth2User, session);
            case "github" -> githubLoginController.handle(oauth2User, authentication, session);
            case "emudhra" -> emudhraLoginController.handle(oauth2User, authentication, session);
            default -> "redirect:/login?oauth2Error=true";
        };
    }
}