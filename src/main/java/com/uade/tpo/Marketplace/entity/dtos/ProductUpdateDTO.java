package com.uade.tpo.Marketplace.entity.dtos;


import jakarta.validation.constraints.*;

public record ProductUpdateDTO(
    String description,
  @Positive Double price,
  @Min(0) Integer stock
) {}
