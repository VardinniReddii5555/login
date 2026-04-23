package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.service.OAuth2LoginSupport;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Controller
public class GithubLoginController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2LoginSupport support;
    private final RestClient restClient = RestClient.builder().build();

    public GithubLoginController(OAuth2AuthorizedClientService authorizedClientService,
                                 OAuth2LoginSupport support) {
        this.authorizedClientService = authorizedClientService;
        this.support = support;
    }

    public String handle(OAuth2User oauth2User, OAuth2AuthenticationToken auth, HttpSession session) {
        String email = oauth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            email = resolveGithubEmail(auth);
        }
        return support.completeLogin(oauth2User, session, email, LoginModes.GITHUB_SSO);
    }

    private String resolveGithubEmail(OAuth2AuthenticationToken auth) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                auth.getAuthorizedClientRegistrationId(), auth.getName());
        if (client == null || client.getAccessToken() == null) {
            return null;
        }
        List<Map<String, Object>> emails = restClient.get()
                .uri("https://api.github.com/user/emails")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.USER_AGENT, "Registration-App")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (emails == null || emails.isEmpty()) {
            return null;
        }
        return emails.stream()
                .filter(item -> Boolean.TRUE.equals(item.get("primary")))
                .map(item -> (String) item.get("email"))
                .findFirst()
                .orElse((String) emails.getFirst().get("email"));
    }
}