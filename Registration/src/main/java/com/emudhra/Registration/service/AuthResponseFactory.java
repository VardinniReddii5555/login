package com.emudhra.Registration.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class AuthResponseFactory {

    public Map<String, String> success(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", message);
        return response;
    }

    public Map<String, String> failure(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "FAIL");
        response.put("message", message);
        return response;
    }
}