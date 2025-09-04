package com.uade.tpo.Marketplace.entity.dtos;

import jakarta.validation.constraints.Min; import jakarta.validation.constraints.NotNull;

public record CompraCreateItemDTO(@NotNull Long productId, @Min(1) int quantity) {}