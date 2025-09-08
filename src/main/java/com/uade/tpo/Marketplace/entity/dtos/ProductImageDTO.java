package com.uade.tpo.Marketplace.entity.dtos;

public record ProductImageDTO(
    Long id,
    String fileName,
    String contentType,
    String url
) {}
