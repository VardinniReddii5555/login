//package com.emudhra.Registration.service;
//
//import com.emudhra.Registration.model.JwtTokenResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class JwtAuthService {
//
//    @Value("${keycloak.jwt.token-url}")
//    private String tokenUrl;
//
//    @Value("${keycloak.jwt.client-id}")
//    private String clientId;
//
//    @Value("${keycloak.jwt.client-secret}")
//    private String clientSecret;
//
//    public JwtTokenResponse authenticate(
//            String username,
//            String password) {
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//
//        body.add("grant_type", "password");
//        body.add("client_id", clientId);
//        body.add("client_secret", clientSecret);
//        body.add("username", username);
//        body.add("password", password);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//        ResponseEntity<JwtTokenResponse> response =
//                restTemplate.exchange(
//                        tokenUrl,
//                        HttpMethod.POST,
//                        request,
//                        JwtTokenResponse.class);
//
//        return response.getBody();
//    }
//}