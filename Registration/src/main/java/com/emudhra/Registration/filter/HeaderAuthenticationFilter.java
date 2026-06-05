package com.emudhra.Registration.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

//public class HeaderAuthenticationFilter {
    @Component
    public class HeaderAuthenticationFilter extends OncePerRequestFilter {
        private static final Logger log = LoggerFactory.getLogger(HeaderAuthenticationFilter.class);
        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull FilterChain filterChain)
                throws ServletException, IOException {
            Enumeration<String> headerNames = request.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
//                System.out.println(header + " = " + request.getHeader(header));
            }
            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof Saml2Authentication samlAuth) {

                Saml2AuthenticatedPrincipal principal =
                        (Saml2AuthenticatedPrincipal) samlAuth.getPrincipal();
            }


            String email = request.getHeader("X-User-Email");

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken auth1 =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority("ROLE_USER")
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth1);
            }
//            log.info("=== HeaderAuthenticationFilter Executed ===");
//            log.info("URI: {}", request.getRequestURI());

            filterChain.doFilter(request, response);
        }
    }

