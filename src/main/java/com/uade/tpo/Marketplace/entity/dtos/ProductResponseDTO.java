package com.uade.tpo.Marketplace.entity.dtos;

public record ProductResponseDTO(
    Long id,
    String name,
    String description,
    Double price,
    int stock,
    Long categoryId,
    String categoryDescription
) {}
