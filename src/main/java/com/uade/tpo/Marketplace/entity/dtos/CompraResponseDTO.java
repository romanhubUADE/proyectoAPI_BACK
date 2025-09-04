package com.uade.tpo.Marketplace.entity.dtos;

import java.time.LocalDateTime; import java.util.List;

public record CompraResponseDTO(Long id, Long userId, double total, LocalDateTime date, List<CompraItemDTO> items) {}