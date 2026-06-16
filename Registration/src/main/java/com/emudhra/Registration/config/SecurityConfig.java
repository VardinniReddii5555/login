package com.emudhra.Registration.config;

import com.emudhra.Registration.service.CustomOAuth2UserService;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.filter.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {
    @Autowired
    private  HeaderAuthenticationFilter headerAuthenticationFilter;
//    @Autowired
//    private SamlHeaderFilter samlHeaderFilter;
    @Autowired
    private SamlDebugFilter samlDebugFilter;
//    @Autowired
//    private OidcDebugFilter oidcDebugFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   LoginAuditService loginAuditService,
                                                   CustomOAuth2UserService customOAuth2UserService)  {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home", "/login", "/register","/logout",
                                            "/otp", "/send-otp", "/verify-otp",
                                            "/google-login", "/api/emudhra/**",
                                            "/saml2/**", "/login/saml2/**",
                                            "/oauth2/**", "/login/oauth2/**",
                                            "/css/**", "/js/**", "/images/**", "/error"
                        ).permitAll()
                        .anyRequest().permitAll()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/oauth2/success", true)
                        .failureUrl("/login?oauth2Error=true")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )

                .saml2Login(saml -> saml
                        .loginPage("/login")
                        .defaultSuccessUrl("/saml/success", true)
                        .failureUrl("/login?samlError=true")
                        .failureHandler(samlFailureHandler())
                )
                .saml2Metadata(Customizer.withDefaults())
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
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
//                .addFilterBefore(
//                        headerAuthenticationFilter,
//                        UsernamePasswordAuthenticationFilter.class
//                )
//                .addFilterBefore(
//                        oidcDebugFilter,
//                        UsernamePasswordAuthenticationFilter.class
//                )
                .addFilterBefore(
                        samlDebugFilter,
                        Saml2WebSsoAuthenticationFilter.class
                )
//                .addFilterAfter(
//                        samlHeaderFilter,
//                        Saml2WebSsoAuthenticationFilter.class
//                )

                .sessionManagement(session -> session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession)
                );

                return http.build();
    }

    private AuthenticationFailureHandler samlFailureHandler() {
        return null;
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