package com.finance.backend.config;

import com.finance.backend.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/h2-console/**", "/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                // Dashboard - VIEWER, ANALYST, ADMIN
                .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**").hasAnyRole("VIEWER", "ANALYST", "ADMIN")
                // Records GET - ANALYST, ADMIN
                .requestMatchers(HttpMethod.GET, "/api/v1/records/**").hasAnyRole("ANALYST", "ADMIN")
                // Records POST/PUT/DELETE - ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/v1/records/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/records/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/records/**").hasRole("ADMIN")
                // Users - ADMIN only
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .userDetailsService(userDetailsService);
        
        // Allow H2 console frames
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
