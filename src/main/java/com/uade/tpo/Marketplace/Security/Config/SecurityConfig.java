// src/main/java/com/uade/tpo/Marketplace/Security/Config/SecurityConfig.java
package com.uade.tpo.Marketplace.Security.Config;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.uade.tpo.Marketplace.Security.Jwt.JwtAuthenticationFilter;

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
            "/api/products",        // <- singular
            "/api/products/**",     // <- recursivo
            "/api/categories",
            "/api/categories/**",
            "/api/products/*/images/**"
        ).permitAll()

        // autenticado
        .requestMatchers("/api/compras/mias", "/api/orders/mias").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/compras/**", "/api/orders/**").authenticated()

        // ADMIN (acepta ADMIN y ROLE_ADMIN)
        .requestMatchers(HttpMethod.POST,
          "/api/products", "/api/products/**",
          "/api/categories", "/api/categories/**",
          "/api/users/**"
      ).hasAnyAuthority("ADMIN","ROLE_ADMIN")

      .requestMatchers(HttpMethod.PUT,
        "/api/products", "/api/products/**",
        "/api/categories", "/api/categories/**",
        "/api/users/**"
    ).hasAnyAuthority("ADMIN","ROLE_ADMIN")

    .requestMatchers(HttpMethod.PATCH,
      "/api/products", "/api/products/**",
      "/api/categories", "/api/categories/**",
      "/api/users/**"
  ).hasAnyAuthority("ADMIN","ROLE_ADMIN")

  .requestMatchers(HttpMethod.DELETE,
    "/api/products", "/api/products/**",
    "/api/categories", "/api/categories/**",
    "/api/users/**"
).hasAnyAuthority("ADMIN","ROLE_ADMIN")

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
    c.setAllowedHeaders(List.of("*"));
    c.setExposedHeaders(List.of("Authorization","Content-Type"));
    c.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }
}