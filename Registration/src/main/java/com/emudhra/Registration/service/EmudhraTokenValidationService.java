package com.emudhra.Registration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
public class EmudhraTokenValidationService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final String expectedIssuer;
    private final String expectedAudience;
    private final ObjectMapper objectMapper;

    public EmudhraTokenValidationService(OAuth2AuthorizedClientService authorizedClientService,
                                         @Value("${app.security.oauth2.emudhra.expected-issuer:}") String expectedIssuer,
                                         @Value("${spring.security.oauth2.client.registration.emudhra.client-id:}") String expectedAudience,
                                         ObjectMapper objectMapper) {
        this.authorizedClientService = authorizedClientService;
        this.expectedIssuer = expectedIssuer;
        this.expectedAudience = expectedAudience;
        this.objectMapper = objectMapper;
    }

    public boolean isValid(OAuth2AuthenticationToken authentication, OAuth2User oauth2User) {

        if (authentication == null
                || oauth2User == null
                || !"emudhra".equalsIgnoreCase(authentication.getAuthorizedClientRegistrationId())) {
            return false;
        }

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        return isAccessTokenValid(authorizedClient);
    }

    public JsonNode verifyApiTokenPayload(String idToken, String accessToken) {
        if (accessToken == null || accessToken.isBlank() || idToken == null || idToken.isBlank()) {
            return null;
        }

        JsonNode claims = extractIdTokenClaims(idToken);
        if (claims == null || !isApiIdTokenClaimSetValid(claims)) {
            return null;
        }

        return claims;
    }

    private boolean isAccessTokenValid(OAuth2AuthorizedClient client) {

        if (client == null) {
            return false;
        }

        OAuth2AccessToken token = client.getAccessToken();

        if (token == null) {
            return false;
        }

        // Check expiry
        return token.getExpiresAt() == null ||
                token.getExpiresAt().isAfter(Instant.now());
    }

    private boolean isIdTokenValid(OidcUser oidcUser) {
        String subject = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        if (subject == null || subject.isBlank() || email == null || email.isBlank()) {
            return false;
        }

        if (!expectedIssuer.isBlank()) {
            String issuer = oidcUser.getIssuer() == null ? null : oidcUser.getIssuer().toString();
            if (issuer == null || !expectedIssuer.equals(issuer)) {
                return false;
            }
        }

        if (!expectedAudience.isBlank()) {
            List<String> audience = oidcUser.getAudience();
            if (audience == null || !audience.contains(expectedAudience)) {
                return false;
            }
        }

        Boolean emailVerified = oidcUser.getEmailVerified();
        return emailVerified == null || emailVerified;
    }

    private JsonNode extractIdTokenClaims(String idToken) {
        try {
            String[] chunks = idToken.split("\\.");
            if (chunks.length < 2) {
                return null;
            }
            byte[] decoded = Base64.getUrlDecoder().decode(chunks[1]);
            return objectMapper.readTree(new String(decoded, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isApiIdTokenClaimSetValid(JsonNode claims) {
        String subject = claims.path("sub").asText("");
        String email = claims.path("email").asText("");
        if (subject.isBlank() || email.isBlank()) {
            return false;
        }

        JsonNode expNode = claims.get("exp");
        if (expNode != null && expNode.canConvertToLong() && expNode.asLong() <= Instant.now().getEpochSecond()) {
            return false;
        }

        if (!expectedIssuer.isBlank()) {
            String issuer = claims.path("iss").asText("");
            if (!expectedIssuer.equals(issuer)) {
                return false;
            }
        }

        if (!expectedAudience.isBlank()) {
            JsonNode audNode = claims.get("aud");
            boolean audienceMatched = false;
            if (audNode != null) {
                if (audNode.isTextual()) {
                    audienceMatched = expectedAudience.equals(audNode.asText());
                } else if (audNode.isArray()) {
                    for (JsonNode node : audNode) {
                        if (expectedAudience.equals(node.asText())) {
                            audienceMatched = true;
                            break;
                        }
                    }
                }
            }
            if (!audienceMatched) {
                return false;
            }
        }

        JsonNode emailVerifiedNode = claims.get("email_verified");
        return emailVerifiedNode == null || emailVerifiedNode.asBoolean();
    }
}