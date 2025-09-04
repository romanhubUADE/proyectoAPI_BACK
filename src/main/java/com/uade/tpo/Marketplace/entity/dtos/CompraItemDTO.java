package com.uade.tpo.Marketplace.entity.dtos;

public record CompraItemDTO(Long productId, String productName, int quantity, double priceUnit, double lineTotal) {}