package com.example.hotdesk.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security, JwaSecurityFilter securityFilter, JwtAuthenticationEntryPoint entryPoint )
            throws Exception {
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                     register->register
                             .requestMatchers(
                                     "/office/create",
                                     "/swagger-ui.html",
                                     "/swagger-ui/**",
                                     "/v3/api-docs",
                                     "/v3/api-docs/**",
                                     "/swagger-resources/**",
                                     "/webjars/**"
                             )
                             .permitAll()
                             .anyRequest()
                             .fullyAuthenticated()
                )
                .sessionManagement(
                        session->session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception->exception
                                .authenticationEntryPoint(entryPoint)
                )
                .build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

// Room CRUD
// Office CRUD
// Desk CRUD
// + JWT security
}
