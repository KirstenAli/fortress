package com.fortress.security;

import com.fortress.entity.Role;
import com.fortress.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Deprecated
public abstract class FortressConfiguration {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    private final String[] whitelist = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "swagger-ui.html",
            "/css/**",
            "/images/**",
            "/js/**",
            "/auth/*",
            "/admin/**",
            "/user/public/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.userDetailsService(userService)
                .sessionManagement(config -> config
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(config -> config.authenticationEntryPoint(getAuthenticationEntryPoint()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authzConfig(http));

        return http.build();
    }

    public abstract void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authz,
                                   HttpSecurity http) throws Exception;

    public AuthenticationEntryPoint getAuthenticationEntryPoint(){
        return (request, response, ex) -> {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ex.getMessage()
            );
        };
    }

    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authzConfig(HttpSecurity http){
        return authz -> {authz
                .requestMatchers(whitelist)
                .permitAll()
                .requestMatchers("/user/admin/**")
                .hasAuthority(Role.ADMIN.toString())
                .requestMatchers("/user/admin/add")
                .denyAll();
            try {
                configure(authz, http);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
