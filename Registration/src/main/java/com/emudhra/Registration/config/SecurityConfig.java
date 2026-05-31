package com.emudhra.Registration.config;

import com.emudhra.Registration.service.CustomOAuth2UserService;
import com.emudhra.Registration.service.LoginAuditService;
//import com.emudhra.Registration.config.SamlSecurityConfig;
import com.emudhra.Registration.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.config.Customizer;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Autowired
    private SamlSecurityConfig samlSecurityConfig;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   LoginAuditService loginAuditService,
                                                   CustomOAuth2UserService customOAuth2UserService)  {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home", "/login", "/register",
                                            "/google-login", "/api/emudhra/**",
                                            "/saml2/**", "/login/saml2/**",
                                            "/oauth2/**", "/login/oauth2/**",
                                            "/css/**", "/js/**", "/images/**", "/error"
                        ).permitAll()
                        .anyRequest().permitAll()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?oauth2Error=true")

                        // 🔥 IMPORTANT FIX (OAuth2 fallback instead of OIDC validation)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
//
                .saml2Login(saml -> saml
                        .loginPage("/login")
                        .defaultSuccessUrl("/saml/success", true)
                        .failureUrl("/login?samlError=true")
                )
//                .saml2Login(saml -> saml
//                        .defaultSuccessUrl("/dashboard", true)
//                )
                .saml2Metadata(Customizer.withDefaults())
                .saml2Logout(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                loginAuditService.closeSessionAudit(session);
                            }
                        })
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }
    // 🔥 CUSTOM USER SERVICE (BYPASSES OIDC ISSUER VALIDATION)
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return userRequest -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("name", "User");

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "name"
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}