package com.aplazo.bnpl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/v1/auth/**").permitAll() // Allow login and signup without authentication
                        .requestMatchers("/v1/users/**").permitAll() // Allow user creationg without authentication
                        .requestMatchers("/v1/customers/**").authenticated() // Protect customer endpoints
                        .requestMatchers("/v1/loans/**").authenticated()
                        .anyRequest().authenticated() // Protect all other endpoints by default
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic authentication
                .formLogin(form -> form.disable()); // Disble form login

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
