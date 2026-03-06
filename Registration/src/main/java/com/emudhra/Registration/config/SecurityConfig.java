package com.emudhra.Registration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

//    private final OidcAuthenticationSuccessHandler oidcAuthenticationSuccessHandler;
    @Autowired
    private OidcAuthenticationSuccessHandler oidcAuthenticationSuccessHandler;

    public SecurityConfig(OidcAuthenticationSuccessHandler oidcAuthenticationSuccessHandler) {
        this.oidcAuthenticationSuccessHandler = oidcAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/google-login", "/oauth2/**", "/login/oauth2/**", "/css/**", "/common/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oidcAuthenticationSuccessHandler)
                )
                .logout(logout -> logout.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}