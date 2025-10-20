// src/main/java/com/uade/tpo/Marketplace/Security/Config/SecurityConfig.java
package com.uade.tpo.Marketplace.Security.Config;

import com.uade.tpo.Marketplace.Security.Jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired private JwtAuthenticationFilter jwtFilter;
  @Autowired private AuthenticationProvider authProvider;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .cors(Customizer.withDefaults())
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // preflight
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // público
        .requestMatchers("/api/v1/auth/**").permitAll()
        .requestMatchers(HttpMethod.GET,
            "/api/products/**",
            "/categories/**",
            "/api/products/*/images/**"
        ).permitAll()

        // autenticado
        .requestMatchers("/api/compras/mias", "/api/orders/mias").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/compras/**", "/api/orders/**").authenticated()

        // SOLO ADMIN para ABM
        .requestMatchers(HttpMethod.POST,   "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.PUT,    "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.PATCH,  "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/categories/**", "/api/users/**").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.GET,    "/api/users/**", "/api/compras/**", "/api/orders/**").hasAuthority("ADMIN")

        // cualquier otra ruta requiere sesión
        .anyRequest().authenticated()
      )
      .authenticationProvider(authProvider)
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*")); // incluye Authorization
    c.setExposedHeaders(List.of("Authorization","Content-Type"));
    c.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }
}
