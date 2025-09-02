package com.uade.tpo.Marketplace.entity.dtos;

import jakarta.validation.constraints.*;

public record UserCreateDTO(
  @NotBlank String firstName,
  @NotBlank String lastName,
  @Email String email,
  @Size(min=6) String password
) {}
