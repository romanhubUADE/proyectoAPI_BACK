package com.uade.tpo.Marketplace.entity.dtos;

import jakarta.validation.constraints.*;

public record UserUpdateDTO(
  String firstName,
  String lastName,
  @Email String email
) {}
