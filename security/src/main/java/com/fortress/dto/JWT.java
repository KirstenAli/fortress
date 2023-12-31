package com.fortress.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class JWT {
    private String accessToken;
    private Long expiration;

    public JWT(String accessToken, Long expiration) {
        this.accessToken = accessToken;
        this.expiration = expiration;
    }
}
