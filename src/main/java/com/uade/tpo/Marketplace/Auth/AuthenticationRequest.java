package com.uade.tpo.Marketplace.Auth;

public record AuthenticationRequest(
    String email, 
    String password)
    {}
