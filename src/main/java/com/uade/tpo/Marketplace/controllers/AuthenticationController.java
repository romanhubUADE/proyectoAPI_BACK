package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.Auth.AuthenticationRequest;
import com.uade.tpo.Marketplace.Auth.AuthenticationResponse;
import com.uade.tpo.Marketplace.Auth.RegisterRequest;
import com.uade.tpo.Marketplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  @Autowired
  private AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest req) {
    return ResponseEntity.ok(service.register(req));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest req) {
    return ResponseEntity.ok(service.authenticate(req));
  }
}