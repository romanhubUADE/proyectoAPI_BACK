package com.uade.tpo.Marketplace.entity.dtos;
import jakarta.validation.constraints.*;
public record ProductCreateDTO(
  @NotBlank(message="name es obligatorio") String name,
  @NotBlank(message="description es obligatoria") String description,
  @NotNull(message="price es obligatorio") @Positive(message="price debe ser > 0") Double price,
  @Min(value=0, message="stock no puede ser negativo") int stock,
  @NotNull(message="categoryId es obligatorio") Long categoryId
) {}
