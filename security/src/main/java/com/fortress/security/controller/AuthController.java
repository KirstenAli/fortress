package com.fortress.security.controller;

import com.fortress.security.dto.AuthRequest;
import com.fortress.security.dto.AuthResponse;
import com.fortress.security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/getToken")
    public ResponseEntity<AuthResponse> getToken(@RequestBody AuthRequest request) {
        var tokenResponse = authService.getToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }
}
