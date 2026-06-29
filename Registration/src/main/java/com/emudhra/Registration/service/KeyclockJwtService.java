package com.emudhra.Registration.service;

import com.emudhra.Registration.model.JwtTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KeyclockJwtService{


        private static final String TOKEN_URL =
                "http://10.80.241.113:9091/realms/EmployeePortal/protocol/openid-connect/token";

        private static final String CLIENT_ID =
                "Employee_portal";

        private static final String CLIENT_SECRET =
                "hzQzuAVtiMjcIRdqCxWaE8jgzVT1g8h0";

        public JwtTokenResponse authenticate(String username, String password) {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", CLIENT_ID);
            body.add("client_secret", CLIENT_SECRET);
            body.add("username", username);
            body.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(body, headers);

            try {

                ResponseEntity<JwtTokenResponse> response =
                        restTemplate.exchange(
                                TOKEN_URL,
                                HttpMethod.POST,
                                request,
                                JwtTokenResponse.class
                        );

                return response.getBody();

            } catch (Exception e) {

                System.out.println("KEYCLOAK JWT AUTH FAILED : " + e.getMessage());
                System.out.println("TOKEN URL = " + TOKEN_URL);
                System.out.println("CLIENT ID = " + CLIENT_ID);
                System.out.println("USERNAME = " + username);
                return null;
            }
        }
    }
