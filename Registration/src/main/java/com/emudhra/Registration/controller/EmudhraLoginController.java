package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.service.OAuth2LoginSupport;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class EmudhraLoginController {

    private final OAuth2LoginSupport support;

    public EmudhraLoginController(OAuth2LoginSupport support) {
        this.support = support;
    }

    public String handle(OAuth2User oauth2User, HttpSession session) {
        String email = support.firstNonBlank(
                oauth2User.getAttribute("email"),
                oauth2User.getAttribute("upn"),
                oauth2User.getAttribute("preferred_username"),
                oauth2User.getAttribute("unique_name"));
        return support.completeLogin(oauth2User, session, email, LoginModes.EMUDHRA_SSO);
    }
}