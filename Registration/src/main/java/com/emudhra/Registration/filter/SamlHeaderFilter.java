package com.emudhra.Registration.filter;

import com.emudhra.Registration.model.Users;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SamlHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof Saml2Authentication samlAuth) {

            Saml2AuthenticatedPrincipal principal =
                    (Saml2AuthenticatedPrincipal) samlAuth.getPrincipal();

            String id = principal.getFirstAttribute("id");
            String username = principal.getFirstAttribute("username");
            String firstName = principal.getFirstAttribute("Firstname");
            String lastName = principal.getFirstAttribute("Lastname");
            String email = principal.getFirstAttribute("email");
            List<String> roles = principal.getAttribute("Role");
            HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(request) {

                @Override
                public String getHeader(String name) {

                    if ("X-User-Id".equalsIgnoreCase(name)) {return id;}
                    if ("X-User-Name".equalsIgnoreCase(name)) {return username;}
                    if ("X-First-Name".equalsIgnoreCase(name)) {return firstName;}
                    if ("X-Last-Name".equalsIgnoreCase(name)) {return lastName;}
                    if ("X-User-Email".equalsIgnoreCase(name)) {return email;}
                    if ("X-User-Roles".equalsIgnoreCase(name)) {return String.join(",", roles);}return super.getHeader(name);
                }
            };
            filterChain.doFilter(wrapped, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}