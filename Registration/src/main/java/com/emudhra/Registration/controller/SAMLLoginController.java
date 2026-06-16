package com.emudhra.Registration.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SAMLLoginController {

    @GetMapping("/saml/successs")
    @ResponseBody
    public String success(Authentication authentication) {

        Saml2AuthenticatedPrincipal principal =
                (Saml2AuthenticatedPrincipal) authentication.getPrincipal();

        System.out.println("NameID = " + principal.getName());
        principal.getAttributes().forEach((k,v) -> {
            System.out.println(k + " = " + v);
        });
        return "SUCCESS";
    }
    private String getUserId(
            Saml2AuthenticatedPrincipal principal) {

        if (principal.getFirstAttribute("email") != null) {
            return principal.getFirstAttribute("email");
        }

        if (principal.getFirstAttribute("username") != null) {
            return principal.getFirstAttribute("username");
        }

        return principal.getName();
    }
}
