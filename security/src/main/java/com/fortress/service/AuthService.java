package com.fortress.service;

import com.fortress.dto.AuthRequest;
import com.fortress.dto.AuthResponse;
import com.fortress.errorhandler.FortressBeacon;
import com.fortress.security.JwtConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtConfig jwtConfig;

    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    public AuthResponse getToken(AuthRequest authRequest) {
        var username = authRequest.getUsername();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
        } catch (AuthenticationException e){
            throw new FortressBeacon(e.getMessage());
        }

        var user = userService.loadUserByUsername(username);
        return jwtConfig.generateAccessToken(user);
    }
}
