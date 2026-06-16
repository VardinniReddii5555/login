package com.emudhra.Registration.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

//@Component
public class OidcDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.startsWith("/saml2/authenticate/")) {

            System.out.println("\n=================================================");
            System.out.println("OIDC DEBUG FILTER");
            System.out.println("=================================================");

            System.out.println("Method       : " + request.getMethod());
            System.out.println("URI          : " + request.getRequestURI());
            System.out.println("URL          : " + request.getRequestURL());
            System.out.println("Query String : " + request.getQueryString());
            HttpSession session = request.getSession(false);

            if (session != null) {
                System.out.println("Session ID   : " + session.getId());
                System.out.println("New Session  : " + session.isNew());
            } else {
                System.out.println("Session      : NULL");
            }

            System.out.println("\nCookies:");
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    System.out.println(
                            cookie.getName() +
                                    " = " +
                                    cookie.getValue());
                }
            }

            System.out.println("\nParameters:");

            Enumeration<String> parameterNames = request.getParameterNames();

            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                String[] values = request.getParameterValues(name);

                if (values != null) {
                    for (String value : values) {
                        System.out.println(name + " = " + value);
                    }
                }
            }
            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            System.out.println("\nAuthentication:");

            if (authentication != null) {
                System.out.println("Authenticated : " + authentication.isAuthenticated());
                System.out.println("Principal     : " + authentication.getPrincipal());
                System.out.println("Authorities   : " + authentication.getAuthorities());
            } else {
                System.out.println("Authentication : NULL");
            }
            System.out.println("=================================================\n");
            filterChain.doFilter(request, response);
            System.out.println("Response Status : " + response.getStatus());

            System.out.println("=================================================\n");
        }
    }
}
