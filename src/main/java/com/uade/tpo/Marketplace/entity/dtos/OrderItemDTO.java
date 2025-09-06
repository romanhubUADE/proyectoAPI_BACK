// OrderItemDTO.java
package com.uade.tpo.Marketplace.entity.dtos;

public record OrderItemDTO(
        Long productId,
        String productName,
        int quantity,
        Double priceUnit,
        Double lineTotal
) {}
