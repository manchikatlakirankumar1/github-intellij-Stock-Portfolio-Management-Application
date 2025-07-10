package com.stockportfoliobackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
// Defines a Spring Security filter chain bean
// Invoked during the application startup to set the web security rules
    @Bean
    //Method configures the HTTP Security filter chain
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Use AntPathRequestMatcher explicitly for the /api/** pattern
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).permitAll()
                        // Allow access to the H2 database console
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .anyRequest().authenticated() //Any other requests need to be logged in
                )
                .headers(headers -> headers.disable()) // Disables Spring security's default HTTP mainly frameOptions
                .cors(cors -> {}); // Enable CORS

        return http.build();
    }
}