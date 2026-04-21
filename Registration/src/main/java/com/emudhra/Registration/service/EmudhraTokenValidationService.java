package com.emudhra.Registration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EmudhraTokenValidationService {

    private final OAuth2AuthorizedClientService clientService;
    private final String clientId;
    private final ObjectMapper objectMapper;

    public EmudhraTokenValidationService(
            OAuth2AuthorizedClientService clientService,
            @Value("${spring.security.oauth2.client.registration.emudhra.client-id}") String clientId,
            ObjectMapper objectMapper) {

        this.clientService = clientService;
        this.clientId = clientId;
        this.objectMapper = objectMapper;
    }

    public boolean isValid(Authentication authentication) {
        try {
            validateToken(authentication.getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 🔥 Main method to validate token
     */
    public JsonNode validateToken(String principalName) {

        try {
            // 1. Load authorized client
            OAuth2AuthorizedClient client =
                    clientService.loadAuthorizedClient("emudhra", principalName);

            if (client == null || client.getAccessToken() == null) {
                throw new RuntimeException("No access token found");
            }

            String token = client.getAccessToken().getTokenValue();

            // 2. Decode JWT
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                throw new RuntimeException("Invalid JWT format");
            }

            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));

            JsonNode jsonNode = objectMapper.readTree(payload);

            // 3. Validate audience (clientId)
            JsonNode audNode = jsonNode.get("aud");

            if (audNode == null || !audNode.asText().equals(clientId)) {
                throw new RuntimeException("Invalid audience");
            }

            // 4. Validate expiry
            long exp = jsonNode.get("exp").asLong();
            long currentTime = System.currentTimeMillis() / 1000;

            if (exp < currentTime) {
                throw new RuntimeException("Token expired");
            }

            // ✅ (Issuer validation removed intentionally)

            return jsonNode;

        } catch (Exception e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }
}