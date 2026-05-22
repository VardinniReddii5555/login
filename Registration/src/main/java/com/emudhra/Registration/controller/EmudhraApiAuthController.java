package com.emudhra.Registration.controller;

import com.emudhra.Registration.constants.LoginModes;
import com.emudhra.Registration.constants.SessionAttribute;
import com.emudhra.Registration.model.Users;
import com.emudhra.Registration.service.EmudhraTokenValidationService;
import com.emudhra.Registration.service.LoginAuditService;
import com.emudhra.Registration.service.SocialLoginService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class EmudhraApiAuthController {

    private final EmudhraTokenValidationService tokenValidationService;
    private final SocialLoginService socialLoginService;
    private final LoginAuditService loginAuditService;
    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUri;

    public EmudhraApiAuthController(EmudhraTokenValidationService tokenValidationService,
                                    SocialLoginService socialLoginService,
                                    LoginAuditService loginAuditService,
                                    @Value("${spring.security.oauth2.client.registration.emudhra.client-id}") String clientId,
                                    @Value("${spring.security.oauth2.client.registration.emudhra.client-secret}") String clientSecret,
                                    @Value("${spring.security.oauth2.client.registration.emudhra.redirect-uri}") String redirectUri,
                                    @Value("${spring.security.oauth2.client.provider.emudhra.token-uri}") String tokenUri) {
        this.tokenValidationService = tokenValidationService;
        this.socialLoginService = socialLoginService;
        this.loginAuditService = loginAuditService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/api/emudhra/state")
    public Map<String, String> generateState(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute(SessionAttribute.EMUDHRA_API_STATE, state);

        Map<String, String> response = new HashMap<>();
        response.put("state", state);
        return response;
    }

    @PostMapping("/api/emudhra/code-login")
    public Map<String, String> codeLogin(@RequestBody Map<String, String> body,
                                         HttpSession session) {
        Map<String, String> response = new HashMap<>();

        String authorizationCode = body.get("authorizationCode");
        if (authorizationCode == null || authorizationCode.isBlank()) {
            response.put("status", "FAIL");
            response.put("message", "Authorization code missing");
            return response;
        }

        if (!isStateValid(body.get("state"), session)) {
            response.put("status", "FAIL");
            response.put("message", "Invalid state");
            return response;
        }

        JsonNode tokenResponse = exchangeCodeForTokens(authorizationCode, body.get("existingUsername"));
        if (tokenResponse == null) {
            response.put("status", "FAIL");
            response.put("message", "Unable to exchange code for tokens");
            return response;
        }

        String accessToken = tokenResponse.path("access_token").asText("");
        String idToken = tokenResponse.path("id_token").asText("");

        JsonNode claims = tokenValidationService.verifyApiTokenPayload(idToken, accessToken);
        if (claims == null) {
            response.put("status", "FAIL");
            response.put("message", "Invalid token");
            return response;
        }
        return completeLogin(session, response, claims, accessToken, idToken, tokenResponse.path("refresh_token").asText(""), authorizationCode);
    }

    @PostMapping("/api/emudhra/verify")
    public Map<String, String> verifyTokenAndLogin(@RequestBody Map<String, String> body,
                                                   HttpSession session) {
        Map<String, String> response = new HashMap<>();

        if (!isStateValid(body.get("state"), session)) {
            response.put("status", "FAIL");
            response.put("message", "Invalid state");
            return response;
        }

        JsonNode claims = tokenValidationService.verifyApiTokenPayload(body.get("idToken"), body.get("accessToken"));
        if (claims == null) {
            response.put("status", "FAIL");
            response.put("message", "Invalid token");
            return response;
        }

        return completeLogin(session, response, claims, body.get("accessToken"), body.get("idToken"), body.get("refreshToken"), body.get("authorizationCode"));
    }

    private Map<String, String> completeLogin(HttpSession session,
                                              Map<String, String> response,
                                              JsonNode claims,
                                              String accessToken,
                                              String idToken,
                                              String refreshToken,
                                              String authorizationCode) {
        String email = claims.path("email").asText("");
        if (email.isBlank()) {
            response.put("status", "FAIL");
            response.put("message", "Unauthorized domain");
            return response;
        }

        String providerUserId = claims.path("sub").asText(email);
        String displayName = claims.path("name").asText(email);

        Users activeUser = socialLoginService.findOrCreateUser(email, displayName, providerUserId, LoginModes.OIDC_SSO);
        loginAuditService.startSessionAudit(activeUser, session, LoginModes.OIDC_SSO);
        session.setAttribute(SessionAttribute.USER, activeUser);

        session.setAttribute("accessToken", accessToken);
        session.setAttribute("refreshToken", refreshToken);
        session.setAttribute("userName", providerUserId);
        session.setAttribute("id_token", idToken);
        session.setAttribute("authorizationCode", authorizationCode);
        session.setAttribute("clientId", providerUserId);
        session.removeAttribute(SessionAttribute.EMUDHRA_API_STATE);

        response.put("status", "SUCCESS");
        response.put("redirect", "/dashboard");
        response.put("message", "Authentication verified");
        return response;
    }

    private boolean isStateValid(String receivedState, HttpSession session) {
        Object expectedStateObj = session.getAttribute(SessionAttribute.EMUDHRA_API_STATE);
        String expectedState = expectedStateObj == null ? null : expectedStateObj.toString();
        return expectedState != null
                && !expectedState.isBlank()
                && expectedState.equals(receivedState);
    }

    private JsonNode exchangeCodeForTokens(String authorizationCode, String existingUsername) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "authorization_code");
        formData.add("code", authorizationCode);
        formData.add("redirect_uri", redirectUri);
        if (existingUsername != null && !existingUsername.isBlank()) {
            formData.add("existingUser", existingUsername);
        }

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    tokenUri,
                    new HttpEntity<>(formData, headers),
                    JsonNode.class);
            return response.getBody();
        } catch (Exception ex) {
            return null;
        }
    }
}