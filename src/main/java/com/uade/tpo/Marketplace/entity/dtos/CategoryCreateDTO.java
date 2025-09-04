package com.uade.tpo.Marketplace.entity.dtos;

import jakarta.validation.constraints.*;

public record CategoryCreateDTO(
  @NotBlank String description
) {}
