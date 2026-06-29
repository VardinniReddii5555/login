//package com.emudhra.Registration.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//public class TokenController {
//
//    @Autowired
//    private OAuth2AuthorizedClientService clientService;
//
//    // 🔥 GET BOTH TOKENS
//    @GetMapping("/token")
//    public Map<String, String> getTokens(OAuth2AuthenticationToken authentication) {
//
//        Map<String, String> tokens = new HashMap<>();
//
//        Object principal = authentication.getPrincipal();
//
//        // ✅ ID TOKEN (only if OIDC)
//        if (principal instanceof OidcUser oidcUser) {
//            String idToken = oidcUser.getIdToken().getTokenValue();
//            tokens.put("idToken", idToken);
//        }
//
//
//        // ✅ ACCESS TOKEN (always)
//        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
//                authentication.getAuthorizedClientRegistrationId(),
//                authentication.getName()
//        );
//
//        if (client != null) {
//            String accessToken = client.getAccessToken().getTokenValue();
//            tokens.put("accessToken", accessToken);
//        } else {
//            tokens.put("accessToken", "NOT_AVAILABLE");
//        }
//
//        return tokens;
//    }
//}