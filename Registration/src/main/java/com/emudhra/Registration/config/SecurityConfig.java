package com.emudhra.Registration.config;

import com.emudhra.Registration.repository.LoginAuditRepository;
import com.emudhra.Registration.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OidcAuthenticationSuccessHandler successHandler,
            LogoutSuccessHandler logoutSuccessHandler) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register",
                                "/oauth2/**", "/login/oauth2/**", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/")
                        .successHandler(successHandler)   // <-- use method parameter
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }

    @Bean
    public OidcAuthenticationSuccessHandler oidcAuthenticationSuccessHandler(
            UserRepository userRepository,
            LoginAuditRepository loginAuditRepository) {

        return new OidcAuthenticationSuccessHandler(userRepository, loginAuditRepository);
    }
}