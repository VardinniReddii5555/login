package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Controller
public class OAuth2Controller {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAuditService loginAuditService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestClient restClient;

    public OAuth2Controller(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            LoginAuditService loginAuditService,
                            OAuth2AuthorizedClientService authorizedClientService) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.loginAuditService = loginAuditService;
            this.authorizedClientService = authorizedClientService;
            this.restClient = RestClient.builder().build();
}
    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User,
                                OAuth2AuthenticationToken authentication,
                                HttpSession session) {
        if (oauth2User == null || authentication == null) {
            return "redirect:/login?oauth2Error=true";
        }

        String registrationId = authentication.getAuthorizedClientRegistrationId();
        String loginMode = resolveRegistrationMode(registrationId);

        String email = extractEmail(oauth2User, authentication, registrationId);
        if (email == null || email.isBlank() || !email.endsWith("@kanchiuniv.ac.in")) {
            return "redirect:/login?oauth2Error=true";
        }

        String name = oauth2User.getAttribute("name");
        String preferredUsername = (name != null && !name.isBlank())
                ? name.replaceAll("\\s+", "")
                : email.split("@")[0];

        if (preferredUsername.length() > 50) {
            preferredUsername = preferredUsername.substring(0, 50);
        }

        Users existingUser = userRepository.findByEmail(email);
        Users activeUser;

        if (existingUser == null) {
            String username = preferredUsername;
            int suffix = 1;
            while (userRepository.existsByUsername(username)) {
                String suffixValue = String.valueOf(suffix);
                int maxBaseLength = 50 - suffixValue.length();
                String base = preferredUsername.length() > maxBaseLength
                        ? preferredUsername.substring(0, maxBaseLength)
                        : preferredUsername;
                username = base + suffixValue;
                suffix++;
            }

            String providerUserId = extractProviderUserId(oauth2User);
            String generatedPassword = passwordEncoder.encode(providerUserId != null ? providerUserId : email);
            Users user = new Users();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(generatedPassword);
            user.setRegistration_mode(loginMode);
            activeUser = userRepository.save(user);
        } else {
            existingUser.setRegistration_mode(loginMode);
            activeUser = userRepository.save(existingUser);
        }

        loginAuditService.startSessionAudit(activeUser, session, loginMode);
        session.setAttribute("user", activeUser);

        return "redirect:/dashboard";
    }
    private String extractEmail(OAuth2User oauth2User,
                                OAuth2AuthenticationToken authentication,
                                String registrationId) {
        String email = oauth2User.getAttribute("email");
        if (email != null && !email.isBlank()) {
            return email;
        }

        if ("github".equalsIgnoreCase(registrationId)) {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    authentication.getAuthorizedClientRegistrationId(),
                    authentication.getName());

            if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                List<Map<String, Object>> emails = restClient.get()
                        .uri("https://api.github.com/user/emails")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

                if (emails != null) {
                    return emails.stream()
                            .filter(item -> Boolean.TRUE.equals(item.get("primary")))
                            .map(item -> (String) item.get("email"))
                            .findFirst()
                            .orElseGet(() -> emails.stream()
                                    .map(item -> (String) item.get("email"))
                                    .findFirst()
                                    .orElse(null));
                }
            }
        }

        return null;
    }

    private String extractProviderUserId(OAuth2User oauth2User) {
        String providerUserId = oauth2User.getAttribute("sub");
        if (providerUserId == null || providerUserId.isBlank()) {
            providerUserId = oauth2User.getAttribute("id");
        }
        return providerUserId;
    }
    private String resolveRegistrationMode(String registrationId) {
        if ("github".equalsIgnoreCase(registrationId)) {
            return LoginModes.GITHUB_SSO;
        }
        if ("emudhra".equalsIgnoreCase(registrationId)) {
            return LoginModes.EMUDHRA_SSO;
        }
        return LoginModes.GOOGLE_SSO;
    }
}
