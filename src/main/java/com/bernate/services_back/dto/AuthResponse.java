package com.bernate.services_back.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer"; // Tipo de token estándar

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}