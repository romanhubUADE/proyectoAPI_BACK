package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.Exceptions.ProductDuplicateException;
import com.uade.tpo.Marketplace.entity.dtos.ProductCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductUpdateDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductResponseDTO;
import com.uade.tpo.Marketplace.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/*import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;*/


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping
    public List<ProductResponseDTO> list() {
        return service.findAll();
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ProductResponseDTO get(@PathVariable Long id) {
        return service.findById(id);
    }

    // Crear producto
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductCreateDTO dto)
            throws ProductDuplicateException {
        var created = service.create(dto);
        return ResponseEntity
                .created(URI.create("/api/products/" + created.id()))
                .body(created);
    }


    @PatchMapping("/api/products/{id}")
    public ProductResponseDTO partialUpdate(@PathVariable Long id,
                                        @Valid @RequestBody ProductUpdateDTO dto) {
    return service.partialUpdate(id, dto);
}

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}