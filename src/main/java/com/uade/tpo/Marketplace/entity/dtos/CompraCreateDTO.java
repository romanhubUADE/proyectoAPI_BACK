package com.uade.tpo.Marketplace.entity.dtos;

import jakarta.validation.constraints.NotEmpty; import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CompraCreateDTO(@NotEmpty List<CompraCreateItemDTO> items) {}