package com.emudhra.Registration.repository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SamlFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        String samlResponse = request.getParameter("SAMLResponse");

        if (samlResponse != null) {
            try {
                byte[] decoded = Base64.getDecoder().decode(samlResponse);
                String xml = new String(decoded, StandardCharsets.UTF_8);

                System.out.println("========== SAML RESPONSE XML ==========");
                System.out.println(xml);
                System.out.println("=======================================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}