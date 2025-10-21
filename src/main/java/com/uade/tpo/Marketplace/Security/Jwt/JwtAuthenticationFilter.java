package com.uade.tpo.Marketplace.Security.Jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse res,
                                  FilterChain chain) throws ServletException, IOException {

    // 1) Dejar pasar preflight CORS sin autenticar
    if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
      chain.doFilter(req, res);
      return;
    }

    // 2) Tomar el encabezado Authorization
    final String authHeader = req.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(req, res);
      return;
    }

    final String jwt = authHeader.substring(7);
    final String username = jwtService.extractUsername(jwt); // suele ser "sub"

    // 3) Si no hay autenticación previa en el contexto, validamos el token
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        UserDetails user = userDetailsService.loadUserByUsername(username);

        // Importante: validar contra el usuario y luego propagar AUTHORITIES reales
        if (jwtService.isTokenValid(jwt, user)) {
          var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          SecurityContextHolder.getContext().setAuthentication(auth);

          // DEBUG: ver usuario y authorities resolvidos
          System.out.println("[JWT] user=" + user.getUsername() +
          " auths=" + user.getAuthorities());
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  user,
                  null,
                  user.getAuthorities() // ← aquí viajan "ADMIN"/"USER"
              );
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          // token inválido, limpiamos por las dudas
          SecurityContextHolder.clearContext();
        }

      } catch (Exception ignored) {
        // No interrumpimos la cadena; dejará 401/403 en capas posteriores según corresponda
        SecurityContextHolder.clearContext();
      }
    }

    // 4) Continuar con el resto de filtros
    chain.doFilter(req, res);
  }
}