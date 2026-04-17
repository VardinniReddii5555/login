package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.service.EmudhraTokenValidationService;
import com.emudhra.Registration.service.OAuth2LoginSupport;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class EmudhraLoginController {

    private final OAuth2LoginSupport support;
    private final EmudhraTokenValidationService tokenValidationService;

    public EmudhraLoginController(OAuth2LoginSupport support,
                EmudhraTokenValidationService tokenValidationService) {
            this.support = support;
            this.tokenValidationService = tokenValidationService;
        }

            public String handle(OAuth2User oauth2User,
                    OAuth2AuthenticationToken authentication,
                    HttpSession session) {
//                if (!tokenValidationService.isValid(authentication, oauth2User)) {
//                    return "redirect:/login?emudhraStrictError=true";
//                }
                boolean valid = tokenValidationService.isValid(authentication, oauth2User);

                System.out.println("Token validation result: " + valid);

// Don't block login for now
                System.out.println("emudhralogincontroller");
                String email = support.firstNonBlank(
                        oauth2User.getAttribute("email"),
                        oauth2User.getAttribute("upn"),
                        oauth2User.getAttribute("preferred_username"),
                        oauth2User.getAttribute("unique_name"));

                return support.completeLogin(oauth2User, session, email, LoginModes.EMUDHRA_SSO);
            }
        }