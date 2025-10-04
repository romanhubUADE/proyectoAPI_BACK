package com.uade.tpo.Marketplace.Security.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.context.annotation.Configuration;

import com.uade.tpo.Marketplace.Security.Jwt.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationFilter jwtFilter;

  @Autowired
  private AuthenticationProvider authProvider;

@Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
          // público
          .requestMatchers("/api/v1/auth/**").permitAll()
          .requestMatchers(org.springframework.http.HttpMethod.GET,
              "/api/products/**",
              "/categories/**",                 
              "/api/products/*/images/**").permitAll()

          // usuario autenticado
          .requestMatchers("/api/compras/mias", "/api/orders/mias").authenticated()
          .requestMatchers(org.springframework.http.HttpMethod.POST,
              "/api/compras/**", "/api/orders/**").authenticated()

          // ADMIN obligatorio
          .requestMatchers(org.springframework.http.HttpMethod.POST,
              "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
          .requestMatchers(org.springframework.http.HttpMethod.PUT,
              "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
          .requestMatchers(org.springframework.http.HttpMethod.PATCH,
              "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
          .requestMatchers(org.springframework.http.HttpMethod.DELETE,
              "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
          .requestMatchers(org.springframework.http.HttpMethod.GET,
              "/api/users/**", "/api/compras/**", "/api/orders/**").hasAuthority("ADMIN")

          .anyRequest().authenticated()
        )
        .authenticationProvider(authProvider)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}