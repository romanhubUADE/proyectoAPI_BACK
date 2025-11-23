package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.Auth.AuthenticationRequest;
import com.uade.tpo.Marketplace.Auth.AuthenticationResponse;
import com.uade.tpo.Marketplace.Auth.RegisterRequest;
import com.uade.tpo.Marketplace.entity.dtos.UserResponseDTO;
import com.uade.tpo.Marketplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.Marketplace.service.UserService;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  @Autowired
  private AuthenticationService service;

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest req) {
    return ResponseEntity.ok(service.register(req));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest req) {
    return ResponseEntity.ok(service.authenticate(req));
  }

  // NUEVO: devuelve el usuario logueado usando el JWT
  @GetMapping("/me")
  public ResponseEntity<UserResponseDTO> me(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      // no hay usuario autenticado
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // auth.getName() es el "username" -> email en tu app
    UserResponseDTO dto = userService.findByEmail(auth.getName());
    return ResponseEntity.ok(dto);
  }
  
}