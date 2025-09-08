package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.Auth.AuthenticationRequest;
import com.uade.tpo.Marketplace.Auth.AuthenticationResponse;
import com.uade.tpo.Marketplace.Auth.RegisterRequest;
import com.uade.tpo.Marketplace.entity.*;
import com.uade.tpo.Marketplace.repository.UserRepository;
import com.uade.tpo.Marketplace.Security.Jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  @Autowired private UserRepository repo;
  @Autowired private PasswordEncoder encoder;
  @Autowired private JwtService jwt;
  @Autowired private AuthenticationManager authManager;

  public AuthenticationResponse register(RegisterRequest req) {
    User user = User.builder()
        .firstName(req.firstName())
        .lastName(req.lastName())
        .email(req.email())
        .password(encoder.encode(req.password()))
        .role(Role.valueOf(req.role().toUpperCase()))
        .build();
    repo.save(user);
    return new AuthenticationResponse(jwt.generateToken(user));
  }

  public AuthenticationResponse authenticate(AuthenticationRequest req) {
    authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    User user = repo.findByEmail(req.email()).orElseThrow();
    return new AuthenticationResponse(jwt.generateToken(user));
  }
}