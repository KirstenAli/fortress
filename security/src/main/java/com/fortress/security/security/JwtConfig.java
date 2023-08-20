package com.fortress.security.security;

import com.fortress.security.dto.AuthResponse;
import com.fortress.security.errorhandler.FortressBeacon;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Getter @Setter
@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationTime;
    private SecretKey key;

    public boolean tokenValid(String token){
        try {
            parseClaims(token);
            return true;
        }
        catch (ExpiredJwtException ex){
            throw new FortressBeacon("Token expired");
        }
    }

    public AuthResponse generateAccessToken(UserDetails userDetails) {
        key = Keys.hmacShaKeyFor(getKeyAsBytes(secretKey));
        var issuedAt = System.currentTimeMillis();
        var expiration = issuedAt + expirationTime;

        var jtw = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(expiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new AuthResponse(jtw, expiration);
    }

    public String getSubject(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    private Jws<Claims> parseClaims(String token) {
        var parser = Jwts.parserBuilder().setSigningKey(getKeyAsBytes(secretKey)).build();
        return parser.parseClaimsJws(token);
    }

    private byte[] getKeyAsBytes(String key){
        return key.getBytes(StandardCharsets.UTF_8);
    }
}
