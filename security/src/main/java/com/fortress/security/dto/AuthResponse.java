package com.fortress.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private Long expiration;

    public AuthResponse(String accessToken, Long expiration) {
        this.accessToken = accessToken;
        this.expiration = expiration;
    }
}
