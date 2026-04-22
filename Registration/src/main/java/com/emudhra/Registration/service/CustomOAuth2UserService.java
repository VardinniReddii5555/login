package com.emudhra.Registration.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final ObjectMapper objectMapper;

    public CustomOAuth2UserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            return delegate.loadUser(userRequest);
        } catch (OAuth2AuthenticationException ex) {
            Map<String, Object> fallbackClaims = extractIdTokenClaims(userRequest);
            if (fallbackClaims.isEmpty()) {
                throw ex;
            }

            Map<String, Object> attributes = new HashMap<>(fallbackClaims);
            attributes.putIfAbsent("name", firstNonBlank(
                    fallbackClaims.get("name"),
                    fallbackClaims.get("preferred_username"),
                    fallbackClaims.get("email"),
                    "User"
            ));

            Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            return new DefaultOAuth2User(authorities, attributes, "name");
        }
    }

    private Map<String, Object> extractIdTokenClaims(OAuth2UserRequest userRequest) {
        Object idTokenObject = userRequest.getAdditionalParameters().get("id_token");
        if (!(idTokenObject instanceof String idToken) || idToken.isBlank()) {
            return Collections.emptyMap();
        }

        String[] tokenChunks = idToken.split("\\.");
        if (tokenChunks.length < 2) {
            return Collections.emptyMap();
        }

        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(tokenChunks[1]);
            return objectMapper.readValue(decodedBytes, new TypeReference<>() {});
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    private String firstNonBlank(Object... values) {
        if (values == null) {
            return null;
        }

        for (Object value : values) {
            if (value == null) {
                continue;
            }
            String text = String.valueOf(value);
            if (!text.isBlank()) {
                return text;
            }
        }
        return null;
    }
}