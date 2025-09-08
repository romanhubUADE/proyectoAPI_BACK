package com.uade.tpo.Marketplace.entity.dtos;

import java.util.List;


public record ProductResponseDTO(
    Long id,
    String name,
    String description,
    Double price,
    int stock,
    Long categoryId,
    String categoryDescription,
    Boolean activo,
    List<ProductImageDTO> images
) {}
