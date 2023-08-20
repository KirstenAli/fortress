package com.fortress.security.security;

import com.fortress.security.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtConfig jwtConfig;

    public JwtTokenFilter(UserService userService, JwtConfig jwtConfig) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String token;
        String userName;

        if (headerHasToken(authorizationHeader)) {
            token = getTokenFromHeader(authorizationHeader);

            if (jwtConfig.tokenValid(token)){
                userName = jwtConfig.getSubject(token);

                if (!authenticationSet()){
                    setAuthentication(userName);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean authenticationSet(){
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private void setAuthentication(String userName){
        UserDetails userDetails = userService.loadUserByUsername(userName);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private String getTokenFromHeader( String authorizationHeader){
        return authorizationHeader.split(" ")[1].trim();
    }
    private boolean headerHasToken(String authorizationHeader){
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }
}
