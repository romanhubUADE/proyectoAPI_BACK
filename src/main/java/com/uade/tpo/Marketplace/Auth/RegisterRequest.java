package com.uade.tpo.Marketplace.Auth;

public record RegisterRequest(
    String firstName, 
    String lastName, 
    String email, 
    String password, 
    String role) {}