package com.uade.tpo.Marketplace.entity.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
    Long id,
    Long userId,
    LocalDateTime date,
    Double total,
    List<OrderItemDTO> items
) {}
