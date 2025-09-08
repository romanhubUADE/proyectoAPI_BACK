package com.uade.tpo.Marketplace.Security.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${application.security.jwt.secretKey}")
  private String secret;

  @Value("${application.security.jwt.expiration}")
  private long expirationMs;

  public String extractUsername(String token) { return extractClaim(token, Claims::getSubject); }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) { return resolver.apply(parse(token)); }

  public String generateToken(UserDetails user) {
    return Jwts.builder()
        .setClaims(Map.of())
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(signKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails user) {
    return user.getUsername().equals(extractUsername(token)) && !isExpired(token);
  }

  private boolean isExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  private Claims parse(String token) {
    return Jwts.parserBuilder().setSigningKey(signKey()).build().parseClaimsJws(token).getBody();
  }

  private Key signKey() {
    byte[] key = (secret.length() % 4 == 0) ? Decoders.BASE64.decode(secret) : secret.getBytes();
    return Keys.hmacShaKeyFor(key);
  }
}